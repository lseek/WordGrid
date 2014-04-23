package com.game.lseek.wordgrid;

import android.util.Log;

/**
 * State transitions for game parser (input is a tokenized line).
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
 * GET_ROUND_TITLE  ROUND_ITEM     GET_ROUND_ITEM    Don't consume line
 *
 * GET_ROUND_ITEM   BLANK_LINE     SKIP_BLANKS       Consume line
 * GET_ROUND_ITEM   TITLE          SKIP_BLANKS       Consume line
 * GET_ROUND_ITEM   ROUND_TITLE    GET_ROUND_TITLE   Don't consume line
 * GET_ROUND_ITEM   ROUND_ITEM     GET_ROUND_ITEM    Add goal to current round, consume line
 */
public class GameFileParser {
    private enum ParserState {
        INITIAL,
        GET_TITLE,
        SKIP_BLANKS,
        GET_ROUND_TITLE,
        GET_ROUND_ITEM,
        ERROR;
    }
    private String LOGTAG = "wordgrid.Game.parser";
    private ParserState currState;


    public GameFileParser() {
        currState = ParserState.INITIAL;
    }


    /*
     * Return the "TaggedLine" if it is not consumed, in which case no new
     * line is read from the file until the current line is consumed.
     */
    public TaggedLine process(TaggedLine line, Game g) {
        Log.d(LOGTAG, String.format("process: currState:%s" currState));
        switch (currState) {
        case INITIAL:
            return initial(line, g);
        case GET_TITLE:
            return getTitle(line, g);
        case  SKIP_BLANKS:
            return SkipBlanks(line, g);
        case GET_ROUND_TITLE:
            return GetRoundTitle(line, g);
        case GET_ROUND_ITEM:
            return GetRoundItem(line, g);
        default:
            // should never come here
            Log.e(LOGTAG, "Parser in unknown state!");
        }

        return null;
    }


    private TaggedLine initial(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                return null;
            case TITLE:
                currState = ParserState.GET_TITLE;
                return line;
            case ROUND_TITLE:
                currState = ParserState.GET_ROUND_TITLE;
                return line;
            case ROUND_ITEM:
                currState = ParserState.GET_ROUND_TITLE;
                return line;
        }
        return line; // should never come here but Java wants it.
    }


    private TaggedLine getTitle(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                currState = ParserState.SKIP_BLANKS;
                return null;
            case TITLE:
                g.title = line.data;
                currState = ParserState.SKIP_BLANKS;
                return null;
            case ROUND_TITLE:
                currState = ParserState.GET_ROUND_TITLE;
                return line;
            case ROUND_ITEM:
                currState = ParserState.GET_ROUND_ITEM;
                return line;
        }
        return line; // should never come here but Java wants it.
    }

    private TaggedLine SkipBlanks(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                return null;
            case TITLE:
                Log.e(LOGTAG, "Saw TITLE in SKIP_BLANKS state!");
                return null;
            case ROUND_TITLE:
                currState = ParserState.GET_ROUND_TITLE;
                return line;
            case ROUND_ITEM:
                currState = ParserState.GET_ROUND_ITEM;
                return line;
        }
        return line;
    }

    private TaggedLine GetRoundTitle(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                currState = ParserState.SKIP_BLANKS;
                return null;
            case TITLE:
                Log.e(LOGTAG, "Saw TITLE in GET_ROUND_TITLE state!");
                currState = ParserState.SKIP_BLANKS;
                return null;
            case ROUND_TITLE:
                g.getNewRound(line.data);
                currState = ParserState.GET_ROUND_ITEM;
                return null;
            case ROUND_ITEM:
                // Should never happen
                Log.w(LOGTAG, "Saw ROUND_ITEM in GET_ROUND_TITLE state!");
                currState = ParserState.GET_ROUND_ITEM;
                return line;
        }
        return line;
    }

    private TaggedLine GetRoundItem(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                currState = ParserState.SKIP_BLANKS;
                return null;
            case TITLE:
                Log.e(LOGTAG, "Saw TITLE in GET_ROUND_ITEM state!");
                currState = ParserState.SKIP_BLANKS;
                return null;
            case ROUND_TITLE:
                currState = ParserState.GET_ROUND_TITLE;
                return line;
            case ROUND_ITEM:
                currState = ParserState.GET_ROUND_ITEM;
                g.getCurrentRound().addGoal(line);
                return null;
        }
        return line;
    }
}

