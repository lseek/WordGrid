package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;


/**
 * State transitions for game parser (input is a tokenized line).
 *
 * In case of parser error, consume and ignore current line.
 *
 * State        Input Line Type    Dest. State         Action
 * -----------------------------------------------------------------------
 * INITIAL          BLANK_LINE     INITIAL            Consume line
 * INITIAL          TITLE          GET_TITLE          Don't consume line
 * INITIAL          ROUND_TITLE    GET_ROUND_TITLE    Don't consume line
 * INITIAL          ROUND_ITEM     GET_ROUND_ITEM     Don't consume line
 *
 * GET_TITLE        BLANK_LINE     SKIP_BLANKS        Consume line
 * GET_TITLE        TITLE          SKIP_BLANKS        Save Title, consume line
 * GET_TITLE        ROUND_TITLE    GET_ROUND_TITLE    Don't consume line
 * GET_TITLE        ROUND_ITEM     GET_ROUND_ITEM     Don't consume line
 *
 * SKIP_BLANKS      BLANK_LINE     SKIP_BLANKS        Consume line
 * SKIP_BLANKS      TITLE          SKIP_BLANKS        Consume line
 * SKIP_BLANKS      ROUND_TITLE    GET_ROUND_TITLE    Don't consume line
 * SKIP_BLANKS      ROUND_ITEM     GET_ROUND_ITEM     Don't consume line
 *
 * GET_ROUND_TITLE  BLANK_LINE     SKIP_BLANKS       Consume line
 * GET_ROUND_TITLE  TITLE          SKIP_BLANKS       Consume line
 * GET_ROUND_TITLE  ROUND_TITLE    GET_ROUND_ITEM    Create new round, consume line
 * GET_ROUND_TITLE  ROUND_ITEM     GET_ROUND_ITEM    (Cannot happen)
 *
 * GET_ROUND_ITEM   BLANK_LINE     SKIP_BLANKS       Consume line
 * GET_ROUND_ITEM   TITLE          SKIP_BLANKS       Consume line
 * GET_ROUND_ITEM   ROUND_TITLE    GET_ROUND_TITLE   Don't consume line
 * GET_ROUND_ITEM   ROUND_ITEM     GET_ROUND_ITEM    Add goal to current round, consume line
 */
public class GameFileParser {
    private String LOGTAG = "wordgrid.parser";

    private enum ParserState {
        INITIAL,
        GET_TITLE,
        SKIP_BLANKS,
        GET_ROUND_TITLE,
        GET_ROUND_ITEM
    }

    private Round currRound;

    private ParserState currState;

    private interface Action {
        // 'run' should return null if it consumes the input line
        public TaggedLine run(TaggedLine line, Game g);
    }

    // Don't consume a line
    private Action nop = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            return line;
        }
    };

    // Just consume a line
    private Action chomp = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            return null;
        }
    };

    // Extract game title
    private Action getTitle = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            g.gameTitle = line.data;
            LOG.d(LOGTAG, String.format("Got Game Title:%s", line.data));
            return null;
        }
    };

    // Extract round title and create new round with it
    private Action getRoundTitle = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            currRound = g.getNewRound(currRound, line.data);
            LOG.d(LOGTAG, String.format("Got Round Title:%s", line.data));
            return null;
        }
    };

    // Extract round item and add it to current round
    private Action getRoundItem = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            if (currRound == null) {
                currRound = g.getNewRound(null, null);
            }
            currRound.addGoal(line);
            LOG.d(LOGTAG, String.format("Parsing Round Item:%s", line.data));
            return null;
        }
    };

    /* An action that warns of a misplaced game title (and consume it) */
    private Action warnMisplacedTitle = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            LOG.e(LOGTAG, "Saw TITLE in invalid state! Ignoring");
            return null;
        }
    };

    private class StateInfo {
        ParserState nextState;
        Action action;

        public StateInfo(ParserState destState, Action handler) {
            nextState = destState;
            action = handler;
        }
    };

    // The state machine:
    //   stateMachine[currState][TagType] gives info about how to handle this
    //   input and transition to next state
    private Map<ParserState, Map<Constants.TagType, StateInfo>> stateMachine;
    {
        stateMachine = new HashMap<ParserState, Map<Constants.TagType, StateInfo>>();

        // Transitions for ParserState.INITIAL state
        Map<Constants.TagType, StateInfo> m = new HashMap<Constants.TagType, StateInfo>();
        m.put(Constants.TagType.BLANK_LINE, new StateInfo(ParserState.INITIAL, chomp));
        m.put(Constants.TagType.TITLE, new StateInfo(ParserState.GET_TITLE, nop));
        m.put(Constants.TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(Constants.TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.INITIAL, m);

        // Transitions for ParserState.GET_TITLE state
        m = new HashMap<Constants.TagType, StateInfo>();
        m.put(Constants.TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(Constants.TagType.TITLE, new StateInfo(ParserState.SKIP_BLANKS, getTitle));
        m.put(Constants.TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(Constants.TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.GET_TITLE, m);

        // Transitions for ParserState.SKIP_BLANKS state
        m = new HashMap<Constants.TagType, StateInfo>();
        m.put(Constants.TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(Constants.TagType.TITLE, new StateInfo(ParserState.SKIP_BLANKS, warnMisplacedTitle));
        m.put(Constants.TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(Constants.TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.SKIP_BLANKS, m);

        // Transitions for ParserState.GET_ROUND_TITLE state
        m = new HashMap<Constants.TagType, StateInfo>();
        m.put(Constants.TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(Constants.TagType.TITLE, new StateInfo(ParserState.SKIP_BLANKS, warnMisplacedTitle));
        m.put(Constants.TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_ITEM, getRoundTitle));
        stateMachine.put(ParserState.GET_ROUND_TITLE, m);

        // Transitions for ParserState.GET_ROUND_ITEM state
        m = new HashMap<Constants.TagType, StateInfo>();
        m.put(Constants.TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(Constants.TagType.TITLE, new StateInfo(ParserState.SKIP_BLANKS, warnMisplacedTitle));
        m.put(Constants.TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(Constants.TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, getRoundItem));
        stateMachine.put(ParserState.GET_ROUND_ITEM, m);
    }


    public GameFileParser() {
        currState = ParserState.INITIAL;
        currRound = null;
    }


    public TaggedLine process(TaggedLine line, Game g) {
        LOG.d(LOGTAG, String.format("process: currState:%s, lineType:%s, data:%s",
                                    currState, line.lineType, line.data));
        StateInfo s = stateMachine.get(currState).get(line.lineType);
        LOG.d(LOGTAG, "State transition from:%s to:%s", currState, s.nextState);
        currState = s.nextState;
        return s.action.run(line, g);
    }

    public Round unprocessedRound() {
        return currRound;
    }
}

