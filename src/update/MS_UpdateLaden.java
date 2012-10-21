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
package update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import mediathek.daten.Daten;
import mediathekServer.tool.MS_Log;

public class MS_UpdateLaden {

    public File updateLaden(String url) {
        File ret = null;
        int timeout = 10000; //10 Sekunden
        URLConnection conn;
        BufferedInputStream in = null;
        FileOutputStream fOut = null;
        try {
            conn = new URL(url).openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestProperty("User-Agent", Daten.getUserAgent());
            File tmpFile = File.createTempFile("mediathek", null);
            tmpFile.deleteOnExit();
            in = new BufferedInputStream(conn.getInputStream());
            fOut = new FileOutputStream(tmpFile);
            final byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                fOut.write(buffer, 0, n);
            }
            ret = tmpFile;
        } catch (Exception ex) {
            ret = null;
            MS_Log.fehlerMeldung(969632140, MS_UpdateLaden.class.getName(), "updateLaden: " + url, ex);
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            }
        }
        return ret;
    }
}
