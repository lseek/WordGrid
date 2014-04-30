package com.game.lseek.wordgrid;

import java.util.regex.Matcher;

import static com.game.lseek.wordgrid.Constants.*;
import com.game.lseek.wordgrid.Constants.TagType;
import com.game.lseek.wordgrid.Constants.HeaderType;

class TaggedLine {
    private String LOGTAG = "wordgrid.Game.TaggedLine";

    TagType lineType;
    HeaderType header; // only for 'headers'
    int lineNum;
    String data;

    // NOTE: line should have been trim()med.
    public TaggedLine(String line, int lineNum) {
        this.lineNum = lineNum;

        LOG.d(LOGTAG, "Parsing line:" + line);
        if (line.length() == 0) {
            LOG.d(LOGTAG, "  Empty line");
            lineType = TagType.BLANK_LINE;
            data = null;
        } else {
            Matcher m = HEADER_RE.matcher(line);
            if (m.matches()) {
                lineType = TagType.HEADER;
                header = HeaderType.valueOf(m.group(TYPE_GRP));
                data = m.group(DATA_GRP);
                LOG.d(LOGTAG, "  type:%s, header:%s, data:%s",
                      lineType, header, data);
            } else {
                m = ROUND_RE.matcher(line);
                if (m.matches()) {
                    LOG.d(LOGTAG, "  ROUND Title");
                    lineType = TagType.ROUND_TITLE;
                    header = HeaderType.NA;
                    data = m.group(DATA_GRP);
                } else {
                    LOG.d(LOGTAG, "  ROUND item");
                    // Round item
                    lineType = TagType.ROUND_ITEM;
                    data = line;
                }
            }
        }
    }
}

