/*
 * MediathekView
 * Copyright (C) 2008 W. Xaver
 * W.Xaver[at]googlemail.com
 * http://zdfmediathk.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package mServer.daten;

import mServer.tool.MServerKonstanten;
import mServer.upload.MServerUpload;
import msearch.tool.GuiFunktionen;
import msearch.tool.MSearchConst;

public class MServerDatenUpload {
    // Konstanten Upload

    public static final String FORMAT_JSON = "json";
    public static final String FORMAT_XML = "xml";
    public static final String UPLOAD = "upload";
    // Array
    public static final String UPLOAD_ART = "upload-art";
    public static final int UPLOAD_ART_NR = 0;
    public static final String UPLOAD_SERVER = "upload-server";
    public static final int UPLOAD_SERVER_NR = 1;
    public static final String UPLOAD_USER = "upload-user";
    public static final int UPLOAD_USER_NR = 2;
    public static final String UPLOAD_PWD = "upload-pwd";
    public static final int UPLOAD_PWD_NR = 3;
    public static final String UPLOAD_DEST_DIR = "upload-dest-dir";
    public static final int UPLOAD_DEST_DIR_NR = 4;
    public static final String UPLOAD_PORT = "upload-port";
    public static final int UPLOAD_PORT_NR = 5;
    public static final String UPLOAD_URL_FILMLISTE = "upload-url-filmliste";
    public static final int UPLOAD_URL_FILMLISTE_NR = 6;
    public static final String UPLOAD_PRIO_FILMLISTE = "upload-prio-filmliste";
    public static final int UPLOAD_PRIO_FILMLISTE_NR = 7;
    public static final String UPLOAD_VORHER_LOESCHEN = "upload-vorher-loeschen";
    public static final int UPLOAD_VORHER_LOESCHEN_NR = 8;
    public static final String UPLOAD_FORMAT = "upload-format";
    public static final int UPLOAD_FORMAT_NR = 9;
    public static final String UPLOAD_MELDEN_URL = "upload-melden-url";
    public static final int UPLOAD_MELDEN_URL_NR = 10;
    public static final String UPLOAD_MELDEN_PWD = "upload-melden-pwd";
    public static final int UPLOAD_MELDEN_PWD_NR = 11;
    public static final int MAX_ELEM = 12;
    public static final String[] UPLOAD_COLUMN_NAMES = {UPLOAD_ART, UPLOAD_SERVER, UPLOAD_USER, UPLOAD_PWD,
        UPLOAD_DEST_DIR, UPLOAD_PORT, UPLOAD_URL_FILMLISTE, UPLOAD_PRIO_FILMLISTE, UPLOAD_VORHER_LOESCHEN,
        UPLOAD_FORMAT, UPLOAD_MELDEN_URL, UPLOAD_MELDEN_PWD};
    public String[] arr = new String[MAX_ELEM];

    public MServerDatenUpload() {
        init();
    }

    private void init() {
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = "";
        }
    }

    public String getServer() {
        return arr[UPLOAD_SERVER_NR];
    }

    public boolean vorherLoeschen() {
        return arr[UPLOAD_VORHER_LOESCHEN_NR].equals(MServerKonstanten.STR_TRUE);
    }

    public String get_Url_Datei_ListeFilmlisten() {
        if (arr[UPLOAD_ART_NR].equals(MServerUpload.UPLOAD_ART_COPY)) {
            return getFilmlisteDestPfadName(MSearchConst.DATEINAME_LISTE_FILMLISTEN);
        } else {
            return GuiFunktionen.addUrl(arr[UPLOAD_URL_FILMLISTE_NR], MSearchConst.DATEINAME_LISTE_FILMLISTEN);
        }
    }

    public String getUrlFilmliste(String dateinameFilmliste) {
        if (arr[UPLOAD_URL_FILMLISTE_NR].isEmpty()) {
            return "";
        } else {
            return GuiFunktionen.addUrl(arr[UPLOAD_URL_FILMLISTE_NR], dateinameFilmliste);
        }
    }

    public String getDestDir() {
        return arr[UPLOAD_DEST_DIR_NR];
    }

    public String getFilmlisteDestPfadName(String dateinameFilmliste) {
        return GuiFunktionen.addsPfad(arr[UPLOAD_DEST_DIR_NR], dateinameFilmliste);
    }

    public String getListeFilmlistenDestPfadName() {
        return GuiFunktionen.addsPfad(arr[UPLOAD_DEST_DIR_NR], MSearchConst.DATEINAME_LISTE_FILMLISTEN);
    }

    public String getPrio() {
        return ((arr[UPLOAD_PRIO_FILMLISTE_NR].equals("")) ? "1" : arr[UPLOAD_PRIO_FILMLISTE_NR]).trim();
    }
}
