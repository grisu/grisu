/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.xml.ws.encoding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.jvnet.mimepull.MIMEPart;

import com.sun.xml.ws.developer.StreamingDataHandler;

/**
 * Implementation of {@link StreamingDataHandler} to access MIME
 * attachments efficiently. Applications can use the additional methods and decide
 * on how to access the attachment data in JAX-WS applications.
 *
 * <p>
 * for e.g.:
 *
 * DataHandler dh = proxy.getData();
 * StreamingDataHandler sdh = (StreamingDataHandler)dh;
 * // readOnce() doesn't store attachment on the disk in some cases
 * // for e.g when only one huge attachment after soap envelope part in MIME message
 * InputStream in = sdh.readOnce();
 * ...
 * in.close();
 * sdh.close();
 *
 * @author Jitendra Kotamraju
 */
public class MIMEPartStreamingDataHandler extends StreamingDataHandler {
	private static final class MyIOException extends IOException {
		private final Exception linkedException;

		MyIOException(Exception linkedException) {
			this.linkedException = linkedException;
		}

		@Override
		public Throwable getCause() {
			return linkedException;
		}
	}

	private static final class StreamingDataSource implements DataSource {
		private final MIMEPart part;

		StreamingDataSource(MIMEPart part) {
			this.part = part;
		}

		public void close() throws IOException {
			part.close();
		}

		public String getContentType() {
			return part.getContentType();
		}

		public InputStream getInputStream() throws IOException {
			return part.read();             //readOnce() ??
		}

		public String getName() {
			return part.getTempFile().getAbsolutePath();
		}

		public OutputStream getOutputStream() throws IOException {
			return null;
		}

		void moveTo(File file) throws IOException {
			part.moveTo(file);
		}

		InputStream readOnce() throws IOException {
			try {
				return part.readOnce();
			} catch(Exception e) {
				throw new MyIOException(e);
			}
		}
	}

	private final StreamingDataSource ds;

	public MIMEPartStreamingDataHandler(MIMEPart part) {
		super(new StreamingDataSource(part));
		ds = (StreamingDataSource)getDataSource();
	}

	@Override
	public void close() throws IOException {
		ds.close();
	}

	@Override
	public void moveTo(File file) throws IOException {
		ds.moveTo(file);
	}

	@Override
	public InputStream readOnce() throws IOException {
		return ds.readOnce();
	}

}