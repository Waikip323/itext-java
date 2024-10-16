/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.pdfa;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IPdfPageFactory;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationContainer;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfA4Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfALogMessageConstant;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends {@link PdfDocument} and is in charge of creating files
 * that comply with the PDF/A standard.
 *
 * Client code is still responsible for making sure the file is actually PDF/A
 * compliant: multiple steps must be undertaken (depending on the
 * {@link PdfConformance}) to ensure that the PDF/A standard is followed.
 *
 * This class will throw exceptions, mostly {@link PdfAConformanceException},
 * and thus refuse to output a PDF/A file if at any point the document does not
 * adhere to the PDF/A guidelines specified by the {@link PdfConformance}.
 */
public class PdfADocument extends PdfDocument {

    private static final IPdfPageFactory pdfAPageFactory = new PdfAPageFactory();

    protected PdfAChecker checker;

    private boolean alreadyLoggedThatObjectFlushingWasNotPerformed = false;

    private boolean alreadyLoggedThatPageFlushingWasNotPerformed = false;
    private PdfConformance conformance;

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param aConformance the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     */
    public PdfADocument(PdfWriter writer, PdfAConformance aConformance, PdfOutputIntent outputIntent) {
        this(writer, aConformance, outputIntent, new DocumentProperties());
    }

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param aConformance the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     * @param properties a {@link com.itextpdf.kernel.pdf.DocumentProperties}
     */
    public PdfADocument(PdfWriter writer, PdfAConformance aConformance, PdfOutputIntent outputIntent, DocumentProperties properties) {
        super(writer, properties);
        this.conformance = new PdfConformance(aConformance);
        setChecker(aConformance);
        addOutputIntent(outputIntent);
    }


    /**
     * No default font for PDF/A documents.
     *
     * @return {@code null}.
     */
    @Override
    public PdfFont getDefaultFont() {
        if (getConformance().isPdfA()) {
            return null;
        }
        return super.getDefaultFont();
    }


    /**
     * Opens a PDF/A document in the stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfADocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Opens a PDF/A document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param properties properties of the stamping process
     */
    public PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        this(reader, writer, properties, false);
    }

    PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties, boolean tolerant) {
        super(reader, writer, properties);
        this.conformance = super.getConformance();
        if (!getConformance().isPdfA() && !tolerant) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.
                            DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);

        }

        setChecker(getConformance().getAConformance());
    }

    /**
     * {@inheritDoc}
     *
     * @param outputIntent {@inheritDoc}
     */
    @Override
    public void addOutputIntent(PdfOutputIntent outputIntent) {
        super.addOutputIntent(outputIntent);
        checker.setPdfAOutputIntentColorSpace(getCatalog().getPdfObject());
    }

    @Override
    public PdfConformance getConformance() {
        return conformance;
    }

    void logThatPdfAPageFlushingWasNotPerformed() {
        if (!alreadyLoggedThatPageFlushingWasNotPerformed) {
            alreadyLoggedThatPageFlushingWasNotPerformed = true;
            // This log message will be printed once for one instance of the document.
            LoggerFactory.getLogger(PdfADocument.class).warn(PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED);
        }
    }

    @Override
    protected void addCustomMetadataExtensions(XMPMeta xmpMeta) {
        if (!getConformance().isPdfA()) {
            super.addCustomMetadataExtensions(xmpMeta);
            return;
        }

        if (this.isTagged()) {
            try {
                if (xmpMeta.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART) != null) {
                    XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PdfAXMPUtil.PDF_UA_EXTENSION);
                    XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
                }
            } catch (XMPException exc) {
                Logger logger = LoggerFactory.getLogger(PdfADocument.class);
                logger.error(IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, exc);
            }
        }
    }

    @Override
    protected void updateXmpMetadata() {
        if (!getConformance().isPdfA()) {
            super.updateXmpMetadata();
            return;
        }

        try {
            XMPMeta xmpMeta = updateDefaultXmpMetadata();
            final PdfAConformance aLevel = checker.getAConformance();
            xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, aLevel.getPart());
            if (aLevel.getLevel() != null) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, aLevel.getLevel());
            }
            if ("4".equals(aLevel.getPart())) {
                xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.REV, PdfConformance.PDF_A_4_REVISION);
            }
            addCustomMetadataExtensions(xmpMeta);
            setXmpMetadata(xmpMeta);
        } catch (XMPException e) {
            Logger logger = LoggerFactory.getLogger(PdfADocument.class);
            logger.error(IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, e);
        }
    }

    @Override
    public void checkIsoConformance(IValidationContext validationContext) {
        setCheckerIfChanged();
        super.checkIsoConformance(validationContext);
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        if (!getConformance().isPdfA()) {
            super.flushObject(pdfObject, canBeInObjStm);
            return;
        }

        markObjectAsMustBeFlushed(pdfObject);
        if (isClosing || checker.objectIsChecked(pdfObject)) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else if (!alreadyLoggedThatObjectFlushingWasNotPerformed) {
            alreadyLoggedThatObjectFlushingWasNotPerformed = true;
            // This log message will be printed once for one instance of the document.
            LoggerFactory.getLogger(PdfADocument.class)
                    .warn(PdfALogMessageConstant.PDFA_OBJECT_FLUSHING_WAS_NOT_PERFORMED);
        }
    }

    /**
     * Sets the checker that defines the requirements of the PDF/A standard
     * depending on conformance.
     *
     * @param aConformance {@link PdfAConformance}
     */
    protected void setChecker(PdfAConformance aConformance) {
        if (!getConformance().isPdfA()) {
            return;
        }
        setChecker(getCorrectCheckerFromConformance(aConformance));
    }

    protected void setChecker(PdfAChecker checker) {
        if (!getConformance().isPdfA()) {
            return;
        }
        this.checker = checker;
        ValidationContainer validationContainer = new ValidationContainer();
        validationContainer.addChecker(checker);
        this.getDiContainer().register(ValidationContainer.class, validationContainer);
    }

    private void setCheckerIfChanged() {
        if (!getConformance().isPdfA()) {
            return;
        }
        if (!getDiContainer().isRegistered(ValidationContainer.class)) {
            return;
        }
        ValidationContainer validationContainer = getDiContainer().getInstance(ValidationContainer.class);
        if (validationContainer != null && !validationContainer.containsChecker(checker)) {
            setChecker(checker);
        }
    }

    private static PdfAChecker getCorrectCheckerFromConformance(PdfAConformance aConformance) {
        PdfAChecker checker;
        switch (aConformance.getPart()) {
            case "1":
                checker = new PdfA1Checker(aConformance);
                break;
            case "2":
                checker = new PdfA2Checker(aConformance);
                break;
            case "3":
                checker = new PdfA3Checker(aConformance);
                break;
            case "4":
                checker = new PdfA4Checker(aConformance);
                break;
            default:
                throw new IllegalArgumentException(PdfaExceptionMessageConstant
                        .CANNOT_FIND_PDFA_CHECKER_FOR_SPECIFIED_NAME);
        }
        return checker;
    }

    /**
     * Initializes tagStructureContext to track necessary information of document's tag structure.
     */
    @Override
    protected void initTagStructureContext() {
        if (getConformance().isPdfA()) {
            tagStructureContext = new TagStructureContext(this, getPdfVersionForPdfA(checker.getAConformance()));
        } else {
            super.initTagStructureContext();
        }
    }

    @Override
    protected IPdfPageFactory getPageFactory() {
        if (getConformance().isPdfA()) {
            return pdfAPageFactory;
        } else {
            return super.getPageFactory();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param appendMode {@inheritDoc}
     */
    @Override
    protected void flushInfoDictionary(boolean appendMode) {
        if (!getConformance().isPdfA() || (!"4".equals(getConformance().getAConformance().getPart()))) {
            super.flushInfoDictionary(appendMode);
        } else if (getCatalog().getPdfObject().get(PdfName.PieceInfo) != null) {
            // Leave only ModDate as required by 6.1.3 File trailer of pdf/a-4 spec
            getDocumentInfo().removeCreationDate();
            super.flushInfoDictionary(appendMode);
        }
    }

    boolean isClosing() {
        return isClosing;
    }

    private static PdfVersion getPdfVersionForPdfA(PdfAConformance aConformance) {
        PdfVersion version;
        switch (aConformance.getPart()) {
            case "1":
                version = PdfVersion.PDF_1_4;
                break;
            case "2":
                version = PdfVersion.PDF_1_7;
                break;
            case "3":
                version = PdfVersion.PDF_1_7;
                break;
            case "4":
                version = PdfVersion.PDF_2_0;
                break;
            default:
                version = PdfVersion.PDF_1_4;
                break;
        }
        return version;
    }
}
