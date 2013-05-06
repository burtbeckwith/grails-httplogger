/**
 * Copyright 2013 TouK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.httplogger

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * @author Marek Maj <marekmaj2@gmail.com>
 */
public class MultiReadHttpServletResponse extends HttpServletResponseWrapper {

    private ServletOutputStream outputStream;
    private ServletOutputStreamImpl copiedOutput;
    private PrintWriter writer;

    public MultiReadHttpServletResponse(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
    }

    public String getCopiedOutput() throws IOException{
        if (copiedOutput != null) {
            return new String(copiedOutput.getCopy(), getCharacterEncoding());
        }
        return new String();
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copiedOutput.flush();
        }
    }

    @Override
    public String getCharacterEncoding() {
        if (getResponse().getCharacterEncoding() == null){
            getResponse().setCharacterEncoding("UTF-8");
        }
        return getResponse().getCharacterEncoding();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copiedOutput = new ServletOutputStreamImpl(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copiedOutput, getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copiedOutput = new ServletOutputStreamImpl(outputStream);
        }

        return copiedOutput;
    }


    private class ServletOutputStreamImpl extends ServletOutputStream {

        private OutputStream os;
        private ByteArrayOutputStream copy = new ByteArrayOutputStream(1024);

        public ServletOutputStreamImpl(OutputStream outputStream) {
            this.os = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            os.write(b);
            copy.write(b);
        }

        public byte[] getCopy() {
            return copy.toByteArray();
        }
    }
}
