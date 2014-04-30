package com.game.lseek.wordgrid;

import java.util.HashMap;
import java.util.Map;

import com.game.lseek.wordgrid.Constants.TagType;
import com.game.lseek.wordgrid.Constants.HeaderType;

/**
 * State transitions for game parser (input is a tokenized line).
 *
 * In case of parser error, consume and ignore current line.
 *
 * State        Input Line Type    Dest. State         Action
 * -----------------------------------------------------------------------
 * INITIAL          BLANK_LINE     INITIAL            Consume line
 * INITIAL          HEADER         GET_HEADER         nop
 * INITIAL          ROUND_TITLE    GET_ROUND_TITLE    nop
 * INITIAL          ROUND_ITEM     GET_ROUND_ITEM     nop
 *
 * GET_HEADER       BLANK_LINE     SKIP_BLANKS        Consume line
 * GET_HEADER       HEADER         GET_HEADER         Save header, no dups
 * GET_HEADER       ROUND_TITLE    GET_ROUND_TITLE    nop
 * GET_HEADER       ROUND_ITEM     GET_ROUND_ITEM     nop
 *
 * SKIP_BLANKS      BLANK_LINE     SKIP_BLANKS        Consume line
 * SKIP_BLANKS      HEADER         SKIP_BLANKS        Consume line (warn)
 * SKIP_BLANKS      ROUND_TITLE    GET_ROUND_TITLE    nop
 * SKIP_BLANKS      ROUND_ITEM     GET_ROUND_ITEM     nop
 *
 * GET_ROUND_TITLE  BLANK_LINE     SKIP_BLANKS       (Cannot happen)
 * GET_ROUND_TITLE  HEADER         SKIP_BLANKS       (Cannot happen)
 * GET_ROUND_TITLE  ROUND_TITLE    GET_ROUND_ITEM    Create new round
 * GET_ROUND_TITLE  ROUND_ITEM     GET_ROUND_ITEM    (Cannot happen)
 *
 * GET_ROUND_ITEM   BLANK_LINE     SKIP_BLANKS       Consume line
 * GET_ROUND_ITEM   HEADER         SKIP_BLANKS       Consume line (warn)
 * GET_ROUND_ITEM   ROUND_TITLE    GET_ROUND_TITLE   nop
 * GET_ROUND_ITEM   ROUND_ITEM     GET_ROUND_ITEM    Add goal to current round
 */
public class GameFileParser {
    private String LOGTAG = "wordgrid.parser";

    private enum ParserState {
        INITIAL,
        GET_HEADER,
        SKIP_BLANKS,
        GET_ROUND_TITLE,
        GET_ROUND_ITEM
    }


    private Round currRound;
    private ParserState currState;

    /*
     * An interface to allow the state machine to execute actions
     */
    private interface Action {
        // 'run' should return null if it consumes the input line
        public TaggedLine run(TaggedLine line, Game g);
    }


    // Just consume a line
    private Action chomp = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            return null;
        }
    };

    // Save a "header"
    private Action saveHeader = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            assert ((line.header == HeaderType.TITLE) ||
                    (line.header == HeaderType.LEVEL) ||
                    (line.header == HeaderType.SIZE));
            if (g.gameInfo.containsKey(line.lineType)) {
                LOG.d(LOGTAG, "Ignoring repeat header:%s in line:%d",
                      line.header, line.lineNum);
            } else {
                g.gameInfo.put(line.header, line.data.toUpperCase());
                LOG.d(LOGTAG, "Got game header:%s", line.data);
            }
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
            LOG.d(LOGTAG, "Parsing Round Item:%s", line.data);
            currRound.addGoal(line);
            return null;
        }
    };

    // Extract round title and create new round with it
    private Action getRoundTitle = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            currRound = g.getNewRound(currRound, line.data);
            LOG.d(LOGTAG, "Got Round Title:%s", line.data);
            return null;
        }
    };

    // Don't consume a line
    private Action nop = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            return line;
        }
    };

    // Warn of an illegally placed header (and consume it)
    private Action warnMisplacedHeader = new Action() {
        @Override
        public TaggedLine run(TaggedLine line, Game g) {
            LOG.e(LOGTAG,
                  "%s cannot be specified here (line:%d) Ignoring it",
                  line.header, line.lineNum);
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
    private Map<ParserState, Map<TagType, StateInfo>> stateMachine;
    {
        stateMachine = new HashMap<ParserState, Map<TagType, StateInfo>>();

        // Transitions for ParserState.INITIAL state
        Map<TagType, StateInfo> m = new HashMap<TagType, StateInfo>();
        m.put(TagType.BLANK_LINE, new StateInfo(ParserState.INITIAL, chomp));
        m.put(TagType.HEADER, new StateInfo(ParserState.GET_HEADER, nop));
        m.put(TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.INITIAL, m);

        // Transitions for ParserState.GET_HEADER state
        m = new HashMap<TagType, StateInfo>();
        m.put(TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(TagType.HEADER, new StateInfo(ParserState.GET_HEADER, saveHeader));
        m.put(TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.GET_HEADER, m);

        // Transitions for ParserState.SKIP_BLANKS state
        m = new HashMap<TagType, StateInfo>();
        m.put(TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(TagType.HEADER, new StateInfo(ParserState.SKIP_BLANKS, warnMisplacedHeader));
        m.put(TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, nop));
        stateMachine.put(ParserState.SKIP_BLANKS, m);

        // Transitions for ParserState.GET_ROUND_TITLE state
        m = new HashMap<TagType, StateInfo>();
        m.put(TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_ITEM, getRoundTitle));
        stateMachine.put(ParserState.GET_ROUND_TITLE, m);

        // Transitions for ParserState.GET_ROUND_ITEM state
        m = new HashMap<TagType, StateInfo>();
        m.put(TagType.BLANK_LINE, new StateInfo(ParserState.SKIP_BLANKS, chomp));
        m.put(TagType.HEADER, new StateInfo(ParserState.SKIP_BLANKS, warnMisplacedHeader));
        m.put(TagType.ROUND_TITLE, new StateInfo(ParserState.GET_ROUND_TITLE, nop));
        m.put(TagType.ROUND_ITEM, new StateInfo(ParserState.GET_ROUND_ITEM, getRoundItem));
        stateMachine.put(ParserState.GET_ROUND_ITEM, m);
    }


    public GameFileParser() {
        currState = ParserState.INITIAL;
        currRound = null;
    }


    public TaggedLine process(TaggedLine line, Game g) {
        LOG.d(LOGTAG,
              "process: currState:%s, lineType:%s, data:%s",
              currState, line.lineType, line.data);
        StateInfo s = stateMachine.get(currState).get(line.lineType);
        LOG.d(LOGTAG, "State transition from:%s to:%s",
              currState, s.nextState);
        currState = s.nextState;
        return s.action.run(line, g);
    }


    public Round unprocessedRound() {
        return currRound;
    }
}

