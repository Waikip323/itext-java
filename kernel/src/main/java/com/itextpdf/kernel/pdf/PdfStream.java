/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.source.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.LoggerFactory;

/**
 * Representation of a stream as described in the PDF Specification.
 */
public class PdfStream extends PdfDictionary {

    private static final long serialVersionUID = -8259929152054328141L;

    protected int compressionLevel;
    // Output stream associated with PDF stream.
    private PdfOutputStream outputStream;
    private InputStream inputStream;
    private long offset;
    private int length = -1;

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param bytes            initial content of {@see PdfOutputStream}.
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(byte[] bytes, int compressionLevel) {
        super();
        setState(MUST_BE_INDIRECT);
        this.compressionLevel = compressionLevel;
        if (bytes != null && bytes.length > 0) {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream(bytes.length));
            this.outputStream.writeBytes(bytes);
        } else {
            this.outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }
    }

    /**
     * Creates a PdfStream instance.
     *
     * @param bytes bytes to write to the PdfStream
     */
    public PdfStream(byte[] bytes) {
        this(bytes, CompressionConstants.UNDEFINED_COMPRESSION);
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <p/>
     * <pre>
     * PdfDocument document = ?;
     * InputStream in = ?;
     * PdfStream stream = new PdfStream(document, in, PdfOutputStream.DEFAULT_COMPRESSION);
     * ?
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param inputStream      the data to write to this stream
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(PdfDocument doc, InputStream inputStream, int compressionLevel) {
        super();
        if (doc == null) {
            throw new PdfException(PdfException.CannotCreatePdfStreamByInputStreamWithoutPdfDocument);
        }
        makeIndirect(doc);
        if (inputStream == null) {
            throw new NullPointerException("inputStream");
        }
        this.inputStream = inputStream;
        this.compressionLevel = compressionLevel;
        put(PdfName.Length, new PdfNumber(-1).makeIndirect(doc));
    }

    /**
     * Creates an efficient stream. No temporary array is ever created. The {@code InputStream}
     * is totally consumed but is not closed. The general usage is:
     * <p/>
     * <pre>
     * PdfDocument document = ?;
     * InputStream in = ?;
     * PdfStream stream = new PdfStream(document, in);
     * stream.flush();
     * in.close();
     * </pre>
     *
     * @param inputStream the data to write to this stream
     */
    public PdfStream(PdfDocument doc, InputStream inputStream) {
        this(doc, inputStream, CompressionConstants.UNDEFINED_COMPRESSION);
    }

    /**
     * Constructs a {@code PdfStream}-object.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public PdfStream(int compressionLevel) {
        this(null, compressionLevel);
    }

    /**
     * Creates an empty PdfStream instance.
     */
    public PdfStream() {
        this(null);
    }

    //NOTE This constructor only for PdfReader.
    PdfStream(long offset, PdfDictionary keys) {
        super();
        this.compressionLevel = CompressionConstants.UNDEFINED_COMPRESSION;
        this.offset = offset;
        putAll(keys);
        PdfNumber length = getAsNumber(PdfName.Length);
        if (length == null) {
            this.length = 0;
        } else {
            this.length = length.intValue();
        }
    }

    /**
     * Gets output stream.
     *
     * @return output stream
     */
    public PdfOutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Gets compression level of this PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets compression level of this PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @param compressionLevel the compression level (0 = best speed, 9 = best compression, -1 is default)
     */
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    @Override
    public byte getType() {
        return STREAM;
    }

    public int getLength() {
        return length;
    }

    /**
     * Gets decoded stream bytes.
     *
     * @return byte[]
     */
    public byte[] getBytes() {
        return getBytes(true);
    }

    /**
     * Gets stream bytes.
     *
     * @param decoded true if to get decoded stream bytes, otherwise false.
     * @return byte content of the {@code PdfStream}. Byte content will be {@code null},
     * if the {@code PdfStream} was created by {@code InputStream}.
     */
    public byte[] getBytes(boolean decoded) {
        if (inputStream != null) {
            LoggerFactory.getLogger(PdfStream.class).warn("PdfStream was created by InputStream." +
                    "getBytes() always returns null in this case");
            return null;
        }
        byte[] bytes = null;
        if (outputStream != null && outputStream.getOutputStream() != null) {
            assert outputStream.getOutputStream() instanceof ByteArrayOutputStream
                    : "Invalid OutputStream: ByteArrayByteArrayOutputStream expected";
            try {
                outputStream.getOutputStream().flush();
                bytes = ((ByteArrayOutputStream) outputStream.getOutputStream()).toByteArray();
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotGetPdfStreamBytes, ioe, this);
            }
        } else if (getIndirectReference() != null) {
            // This logic makes sense only for the case when PdfStream was created by reader and in this
            // case PdfStream instance always has indirect reference and is never in the MustBeIndirect state
            PdfReader reader = getIndirectReference().getReader();
            if (reader != null) {
                try {
                    bytes = reader.readStreamBytes(this, decoded);
                } catch (IOException ioe) {
                    throw new PdfException(PdfException.CannotGetPdfStreamBytes, ioe, this);
                }
            }
        }
        return bytes;
    }

    /**
     * Sets <code>bytes</code> as stream's content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     *
     * @param bytes new content for stream; if <code>null</code> then stream's content will be discarded
     */
    public void setData(byte[] bytes) {
        setData(bytes, false);
    }

    /**
     * Sets or appends <code>bytes</code> to stream content.
     * Could not be used with streams which were created by <code>InputStream</code>.
     *
     * @param bytes  new content for stream; if <code>null</code> and <code>append</code> is false then
     *               stream's content will be discarded
     * @param append if set to true then <code>bytes</code> will be appended to the end,
     *               rather then replace original content
     */
    public void setData(byte[] bytes, boolean append) {
        if (inputStream != null) {
            throw new PdfException(PdfException.CannotSetDataToPdfstreamWhichWasCreatedByInputStream);
        }

        boolean outputStreamIsUninitialized = outputStream == null;
        if (outputStreamIsUninitialized) {
            outputStream = new PdfOutputStream(new ByteArrayOutputStream());
        }

        if (append) {
            if (outputStreamIsUninitialized
                    && getIndirectReference() != null && getIndirectReference().getReader() != null) {
                // here is the same as in the getBytes() method: this logic makes sense only when stream is created
                // by reader and in this case indirect reference won't be null and stream is not in the MustBeIndirect state.

                byte[] oldBytes;
                try {
                    oldBytes = getBytes();
                } catch (PdfException ex) {
                    throw new PdfException(PdfException.CannotReadAStreamInOrderToAppendNewBytes, ex);
                }
                offset = 0;
                outputStream.writeBytes(oldBytes);
            }

            if (bytes != null) {
                outputStream.writeBytes(bytes);
            }
        } else {
            if (bytes != null) {
                outputStream.assignBytes(bytes, bytes.length);
            } else {
                outputStream.reset();
            }
        }

        // Only when we remove old filter will the compression logic be triggered on flushing the stream
        remove(PdfName.Filter);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream makeIndirect(PdfDocument document) {
        return (PdfStream) super.makeIndirect(document);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        return (PdfStream) super.makeIndirect(document, reference);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream copyTo(PdfDocument document) {
        return (PdfStream) super.copyTo(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public PdfStream copyTo(PdfDocument document, boolean allowDuplicating) {
        return (PdfStream) super.copyTo(document, allowDuplicating);
    }

    @Override
    protected PdfStream newInstance() {
        return new PdfStream();
    }

    protected long getOffset() {
        return offset;
    }

    /**
     * Update length manually in case its correction. {@see PdfReader.checkPdfStreamLength()} method.
     */
    protected void updateLength(int length) {
        this.length = length;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfStream stream = (PdfStream) from;
        assert inputStream == null : "Try to copy the PdfStream that has been just created.";
        byte[] bytes = stream.getBytes(false);
        try {
            outputStream.write(bytes);
        } catch (IOException ioe) {
            throw new PdfException(PdfException.CannotCopyObjectContent, ioe, stream);
        }
    }

    protected void initOutputStream(java.io.OutputStream stream) {
        if (getOutputStream() == null && inputStream == null)
            outputStream = new PdfOutputStream(stream != null ? stream : new ByteArrayOutputStream());
    }

    /**
     * Release content of PdfStream.
     */
    protected void releaseContent() {
        super.releaseContent();
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
    }

    protected InputStream getInputStream() {
        return inputStream;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (inputStream == null || inputStream instanceof Serializable) {
            out.defaultWriteObject();
        } else {
            InputStream backup = inputStream;
            inputStream = null;
            LoggerFactory.getLogger(getClass()).warn(LogMessageConstant.INPUT_STREAM_CONTENT_IS_LOST_ON_PDFSTREAM_SERIALIZATION);
            inputStream = backup;
        }

    }
}
