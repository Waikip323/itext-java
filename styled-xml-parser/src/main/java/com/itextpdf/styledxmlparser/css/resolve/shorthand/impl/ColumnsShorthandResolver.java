package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shorthand resolver for the column property.
 * This property is a shorthand for the column-count and column-width properties.
 */
public class ColumnsShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnsShorthandResolver.class);

    /**
     * Creates a new {@link ColumnsShorthandResolver} instance.
     */
    public ColumnsShorthandResolver() {
        //empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.COLUMN_COUNT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, shorthandExpression)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMNS, shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.COLUMNS, shorthandExpression);
        }

        final String[] properties = shorthandExpression.split(" ");
        if (properties.length > 2) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMNS, shorthandExpression);
        }
        List<CssDeclaration> result = new ArrayList<>(2);
        for (String property : properties) {
            CssDeclaration declaration = processProperty(property);
            if (declaration != null) {
                result.add(declaration);
            }
            if (declaration == null && !CommonCssConstants.AUTO.equals(property)) {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.COLUMNS, shorthandExpression);
            }
        }
        if (result.size() == 2 && result.get(0).getProperty().equals(result.get(1).getProperty())) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMNS, shorthandExpression);
        }
        return result;
    }

    private static CssDeclaration processProperty(String value) {
        if (CssTypesValidationUtils.isMetricValue(value) || CssTypesValidationUtils.isRelativeValue(value)) {
            return new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, value);
        }
        if (CssTypesValidationUtils.isNumber(value)) {
            return new CssDeclaration(CommonCssConstants.COLUMN_COUNT, value);
        }
        return null;
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }
}
