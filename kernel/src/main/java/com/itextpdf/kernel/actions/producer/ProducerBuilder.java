/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions.producer;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.actions.events.ConfirmedEventWrapper;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class is used for producer line building.
 */
public final class ProducerBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerBuilder.class);

    private static final String CURRENT_DATE = "currentDate";
    private static final String USED_PRODUCTS = "usedProducts";
    private static final String COPYRIGHT_SINCE = "copyrightSince";
    private static final String COPYRIGHT_TO = "copyrightTo";

    private static final char FORMAT_DELIMITER = ':';

    private static final String MODIFIED_USING = "; modified using ";

    /**
     * Pattern is used to search a placeholders. Currently it searches substrings started with
     * <code>${</code> and ended with <code>}</code> without <code>}</code> character inside.
     * These substrings are interpreted as placeholders and the first group is the content of the
     * placeholder.
     */
    private static final String PATTERN_STRING = "\\$\\{([^}]*)}";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);

    private static final Map<String, IPlaceholderPopulator> PLACEHOLDER_POPULATORS;

    static {
        final Map<String, IPlaceholderPopulator> populators = new HashMap<>();
        populators.put(CURRENT_DATE, new CurrentDatePlaceholderPopulator());
        populators.put(USED_PRODUCTS, new UsedProductsPlaceholderPopulator());
        populators.put(COPYRIGHT_SINCE, new CopyrightSincePlaceholderPopulator());
        populators.put(COPYRIGHT_TO, new CopyrightToPlaceholderPopulator());

        PLACEHOLDER_POPULATORS = Collections.unmodifiableMap(populators);
    }

    private ProducerBuilder() { }

    /**
     * Modifies an old producer line according to events registered for the document. Format of the
     * new producer line will be defined by the first event in the list. Placeholder will be
     * replaced and merged all together
     *
     * @param events list of events wrapped with {@link ConfirmedEventWrapper} registered for
     *               the document
     * @param oldProducer is an old producer line. If <code>null</code> or empty, will be replaced
     *                    with a new one. Otherwise new line will be attached with
     *                    <code>modified using</code> prefix. If old producer line already contains
     *                    <code>modified using</code> substring, it will be overriden with a new one
     * @return modified producer line
     */
    public static String modifyProducer(List<ConfirmedEventWrapper> events, String oldProducer) {
        final String newProducer = buildProducer(events);
        if (oldProducer == null || oldProducer.isEmpty()) {
            return newProducer;
        } else {
            return oldProducer + MODIFIED_USING + newProducer;
        }
    }

    private static String buildProducer(List<ConfirmedEventWrapper> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException(KernelExceptionMessageConstant.NO_EVENTS_WERE_REGISTERED_FOR_THE_DOCUMENT);
        }

        // we expects here that the first event was thrown by
        // the addon which may be considered as entry point of
        // document processing
        final String producer = events.get(0).getProducerLine();

        return populatePlaceholders(producer, events);
    }

    private static String populatePlaceholders(String producerLine, List<ConfirmedEventWrapper> events) {
        int lastIndex = 0;
        Matcher matcher = PATTERN.matcher(producerLine);

        final StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(producerLine.substring(lastIndex, matcher.start()));
            lastIndex = matcher.end();

            final String placeholder = matcher.group(1);
            final int delimiterPosition = placeholder.indexOf(FORMAT_DELIMITER);
            String placeholderName;
            String parameter;
            if (placeholder.indexOf(FORMAT_DELIMITER) != -1) {
                placeholderName = placeholder.substring(0, delimiterPosition);
                parameter = placeholder.substring(delimiterPosition + 1);
            } else {
                placeholderName = placeholder;
                parameter = null;
            }

            final IPlaceholderPopulator populator = PLACEHOLDER_POPULATORS.get(placeholderName);
            if (populator == null) {
                LOGGER.info(MessageFormatUtil.format(
                        KernelLogMessageConstant.UNKNOWN_PLACEHOLDER_WAS_IGNORED, placeholderName));
            } else {
                builder.append(populator.populate(events, parameter));
            }
        }
        builder.append(producerLine.substring(lastIndex));
        return builder.toString();
    }
}
