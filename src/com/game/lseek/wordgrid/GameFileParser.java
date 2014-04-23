package com.game.lseek.wordgrid;

import android.util.Log;

/**
 * State transitions for game parser (input is a tokenized line).
 *
 * State        Input Tag Type              Dest. State
 *
 * Initial      BLANK_LINE                  Initial
 * Initial      TITLE                       GetTitle
 * Initial      ROUND_TITLE                 GetRoundTitle
 * Initial      ROUND_ITEM                  GetRoundItems
 *
 * GetTitle     BLANK_LINE                  SkipBlanks
 * GetTitle     TITLE                       SkipBlanks
 * GetTitle     ROUND_TITLE                 GetRoundTitle
 * GetTitle     ROUND_ITEM                  GetRoundItem
 *
 * SkipBlanks   BLANK_LINE                  SkipBlanks
 * SkipBlanks   ROUND_TITLE                 GetRoundTitle
 * SkipBlanks   ROUND_ITEM                  GetRoundItem
 * SkipBlanks   TITLE                       Error
 *
 * GetRoundTitle  BLANK_LINE                (Delete this round)
 * GetRoundTitle  ROUND_ITEM                GetRoundItem
 * GetRoundTitle  TITLE                     Error
 * GetRoundTitle  ROUND_TITLE               (Delete this round)
 *
 * GetRoundItem BLANK_LINE                  SkipBlanks
 * GetRoundItem ROUND_TITLE                 GetRoundTitle
 * GetRoundItem ROUND_ITEM                  GetRoundItem
 * GetRoundItem TITLE                       Error
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

    private ParserState currState = ParserState.INITIAL;


    public GameFileParser() {;}


    /*
     * Return the "TaggedLine" if it is not consumed, in which case no new
     * line is read from the file until the current line is consumed.
     */
    public TaggedLine process(TaggedLine line, Game g) {
        if (currState == ParserState.INITIAL) {
            return initial(line, g);
        } else if (currState == ParserState.GET_TITLE) {
            return getTitle(line, g);
        } else if (currState == ParserState.SKIP_BLANKS) {
            return SkipBlanks(line, g);
        } else if (currState == ParserState.GET_ROUND_TITLE) {
            return GetRoundTitle(line, g);
        } else if (currState == ParserState.GET_ROUND_ITEM) {
            return GetRoundItem(line, g);
        }

        // TODO: Raise some invalid state exception
        Log.e(LOGTAG, "Parser encountered error");
        return null;
    }


    private TaggedLine initial(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                // consume the empty line, don't change state.
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
                Log.e(LOGTAG, "TITLE cannot be declared here, skipping");
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
                return null;
            case TITLE:
                Log.e(LOGTAG, "TITLE cannot be declared here, skipping");
                return null;
            case ROUND_TITLE:
                g.getNewRound(line.data);
                currState = ParserState.GET_ROUND_TITLE;
                return null;
            case ROUND_ITEM:
                currState = ParserState.GET_ROUND_ITEM;
                return line;
        }
        return line;
    }

    private TaggedLine GetRoundItem(TaggedLine line, Game g) {
        switch (line.lineType) {
            case BLANK_LINE:
                return null;
            case TITLE:
                Log.e(LOGTAG, "TITLE cannot be declared here, skipping");
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

