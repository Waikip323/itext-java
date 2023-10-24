/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.forms.fields;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;


/**
 * An AcroForm field containing signature data.
 */
public class PdfSignatureFormField extends PdfFormField {

    /**
     * Indicates if we need to reuse the existing appearance as a background layer.
     */
    private boolean reuseAppearance = false;

    /**
     * Background level of the signature appearance.
     */
    private PdfFormXObject n0;

    /**
     * Signature appearance layer that contains information about the signature.
     */
    private PdfFormXObject n2;

    protected PdfSignatureFormField(PdfDocument pdfDocument) {
        super(pdfDocument);
    }

    protected PdfSignatureFormField(PdfWidgetAnnotation widget, PdfDocument pdfDocument) {
        super(widget, pdfDocument);
    }

    protected PdfSignatureFormField(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Returns <code>Sig</code>, the form type for signature form fields.
     * 
     * @return the form type, as a {@link PdfName}
     */
    @Override
    public PdfName getFormType() {
        return PdfName.Sig;
    }

    /**
     * Adds the signature to the signature field.
     * 
     * @param value the signature to be contained in the signature field, or an indirect reference to it
     * @return the edited field
     */
    public PdfSignatureFormField setValue(PdfObject value) {
        put(PdfName.V, value);
        return this;
    }

    /**
     * Gets the {@link PdfSigFieldLock}, which contains fields that
     * must be locked if the document is signed.
     * 
     * @return a dictionary containing locked fields.
     * @see PdfSigFieldLock
     */
    public PdfSigFieldLock getSigFieldLockDictionary() {
        PdfDictionary sigLockDict = (PdfDictionary) getPdfObject().get(PdfName.Lock);
        return sigLockDict == null ? null : new PdfSigFieldLock(sigLockDict);
    }

    /**
     * Sets the background layer that is present when creating the signature field.
     *
     * @param n0 layer xObject.
     *
     * @return this same {@link PdfSignatureFormField} instance.
     */
    public PdfSignatureFormField setBackgroundLayer(PdfFormXObject n0) {
        this.n0 = n0;
        regenerateField();
        return this;
    }

    /**
     * Sets the signature appearance layer that contains information about the signature, e.g. the line art for the
     * handwritten signature, the text giving the signer’s name, date, reason, location and so on.
     *
     * @param n2 layer xObject.
     *
     * @return this same {@link PdfSignatureFormField} instance.
     */
    public PdfSignatureFormField setSignatureAppearanceLayer(PdfFormXObject n2) {
        this.n2 = n2;
        regenerateField();
        return this;
    }

    /**
     * Indicates that the existing appearances needs to be reused as a background.
     *
     * @param reuseAppearance is an appearances reusing flag value to set.
     * @return this same {@link PdfSignatureFormField} instance.
     */
    public PdfSignatureFormField setReuseAppearance(boolean reuseAppearance) {
        this.reuseAppearance = reuseAppearance;
        return this;
    }

    /**
     * Gets the background layer that is present when creating the signature field if it was set.
     *
     * @return n0 layer xObject.
     */
    PdfFormXObject getBackgroundLayer() {
        return n0;
    }

    /**
     * Gets the signature appearance layer that contains information about the signature if it was set.
     *
     * @return n2 layer xObject.
     */
    PdfFormXObject getSignatureAppearanceLayer() {
        return n2;
    }

    /**
     * Indicates if the existing appearances needs to be reused as a background.
     *
     * @return appearances reusing flag value.
     */
    boolean isReuseAppearance() {
        return reuseAppearance;
    }

}
