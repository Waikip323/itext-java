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
package com.itextpdf.io.image;

import com.itextpdf.io.IOException;
import com.itextpdf.io.codec.CCITTG4Encoder;
import com.itextpdf.io.codec.TIFFFaxDecoder;

import java.util.HashMap;
import java.util.Map;

public final class RawImageHelper {

    public static void updateImageAttributes(RawImageData image, Map<String, Object> additional) {
        if (!image.isRawImage())
            throw new IllegalArgumentException("Raw image expected.");
        // will also have the CCITT parameters
        int colorSpace = image.getColorSpace();
        int typeCCITT = image.getTypeCcitt();
        if (typeCCITT > 0xff) {
            if (!image.isMask())
                image.setColorSpace(1);
            image.setBpc(1);
            image.setFilter("CCITTFaxDecode");
            int k = typeCCITT - RawImageData.CCITTG3_1D;
            Map<String, Object> decodeparms = new HashMap<>();
            if (k != 0)
                decodeparms.put("K", k);
            if ((colorSpace & RawImageData.CCITT_BLACKIS1) != 0)
                decodeparms.put("BlackIs1", true);
            if ((colorSpace & RawImageData.CCITT_ENCODEDBYTEALIGN) != 0)
                decodeparms.put("EncodedByteAlign", true);
            if ((colorSpace & RawImageData.CCITT_ENDOFLINE) != 0)
                decodeparms.put("EndOfLine", true);
            if ((colorSpace & RawImageData.CCITT_ENDOFBLOCK) != 0)
                decodeparms.put("EndOfBlock", false);
            decodeparms.put("Columns", image.getWidth());
            decodeparms.put("Rows", image.getHeight());
            image.decodeParms = decodeparms;
        } else {
            switch (colorSpace) {
                case 1:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0};
                    break;
                case 3:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0};
                    break;
                case 4:
                default:
                    if (image.isInverted())
                        image.decode = new float[]{1, 0, 1, 0, 1, 0, 1, 0};
            }
            if (additional != null) {
                image.setImageAttributes(additional);
            }
            if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8))
                image.setColorSpace(-1);
            if (image.isDeflated()) {
                image.setFilter("FlateDecode");
            }
        }
    }

    /**
     * Update original image with Raw Image parameters.
     *
     * @param image to update its parameters with Raw Image parameters.
     * @param width the exact width of the image
     * @param height the exact height of the image
     * @param components 1,3 or 4 for GrayScale, RGB and CMYK
     * @param bpc bits per component. Must be 1,2,4 or 8
     * @param data the image data
     * @throws IOException on error
     */
    protected static void updateRawImageParameters(RawImageData image, int width, int height, int components,
                                                   int bpc, byte[] data) {
        image.setHeight(height);
        image.setWidth(width);
        if (components != 1 && components != 3 && components != 4)
            throw new IOException(IOException.ComponentsMustBe1_3Or4);
        if (bpc != 1 && bpc != 2 && bpc != 4 && bpc != 8)
            throw new IOException(IOException.BitsPerComponentMustBe1_2_4or8);
        image.setColorSpace(components);
        image.setBpc(bpc);
        image.data = data;
    }

    protected static void updateRawImageParameters(RawImageData image, int width, int height, int components,
                                                int bpc, byte[] data, int[] transparency) {
        if (transparency != null && transparency.length != components * 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        if (components == 1 && bpc == 1) {
            byte[] g4 = CCITTG4Encoder.compress(data, width, height);
            updateRawImageParameters(image, width, height, false, RawImageData.CCITTG4,
                    RawImageData.CCITT_BLACKIS1, g4, transparency);
        } else {
            updateRawImageParameters(image, width, height, components, bpc, data);
            image.setTransparency(transparency);
        }
    }

    protected static void updateRawImageParameters(RawImageData image, int width, int height, boolean reverseBits,
                                                int typeCCITT, int parameters, byte[] data, int[] transparency) {
        if (transparency != null && transparency.length != 2)
            throw new IOException(IOException.TransparencyLengthMustBeEqualTo2WithCcittImages);
        updateCcittImageParameters(image, width, height, reverseBits, typeCCITT, parameters, data);
        image.setTransparency(transparency);
    }

    protected static void updateCcittImageParameters(RawImageData image, int width, int height, boolean reverseBits, int typeCcitt, int parameters, byte[] data) {
        if (typeCcitt != RawImageData.CCITTG4 && typeCcitt != RawImageData.CCITTG3_1D && typeCcitt != RawImageData.CCITTG3_2D)
            throw new IOException(IOException.CcittCompressionTypeMustBeCcittg4Ccittg3_1dOrCcittg3_2d);
        if (reverseBits)
            TIFFFaxDecoder.reverseBits(data);
        image.setHeight(height);
        image.setWidth(width);
        image.setColorSpace(parameters);
        image.setTypeCcitt(typeCcitt);
        image.data = data;
    }
}
