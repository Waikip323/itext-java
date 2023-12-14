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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.property.BlendMode;
import com.itextpdf.styledxmlparser.CommonAttributeConstants;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.pseudo.CssPseudoElementNode;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.parser.Tag;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupElementNode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssUtilsTest extends ExtendedITextTest {
    private static float EPS = 0.0001f;

    @Test
    public void extractShorthandPropertiesFromEmptyStringTest() {
        String sourceString = "";
        List<List<String>> expected = new ArrayList<>();
        expected.add(new ArrayList<String>());

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void extractShorthandPropertiesFromStringWithOnePropertyTest() {
        String sourceString = "square inside url('sqpurple.gif')";
        List<List<String>> expected = new ArrayList<>();
        List<String> layer = new ArrayList<>();
        layer.add("square");
        layer.add("inside");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void extractShorthandPropertiesFromStringWithMultiplyPropertiesTest() {
        String sourceString = "center no-repeat url('sqpurple.gif'), #eee 35% url('sqpurple.gif')";
        List<List<String>> expected = new ArrayList<>();
        List<String> layer = new ArrayList<>();
        layer.add("center");
        layer.add("no-repeat");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        layer = new ArrayList<>();
        layer.add("#eee");
        layer.add("35%");
        layer.add("url('sqpurple.gif')");
        expected.add(layer);

        Assert.assertEquals(expected, CssUtils.extractShorthandProperties(sourceString));
    }

    @Test
    public void parseAbsoluteLengthFromNAN() {
        String value = "Definitely not a number";
        Exception e = Assert.assertThrows(StyledXMLParserException.class,
                () -> CssUtils.parseAbsoluteLength(value)
        );
        Assert.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "Definitely not a number"),
                e.getMessage());
    }

    @Test
    public void parseAbsoluteLengthFromNull() {
        String value = null;
        Exception e = Assert.assertThrows(StyledXMLParserException.class,
                () -> CssUtils.parseAbsoluteLength(value)
        );
        Assert.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "null"), e.getMessage());
    }

    @Test
    public void parseAbsoluteLengthFrom10px() {
        String value = "10px";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.PX);
        float expected = 7.5f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10cm() {
        String value = "10cm";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.CM);
        float expected = 283.46457f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10in() {
        String value = "10in";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.IN);
        float expected = 720.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10pt() {
        String value = "10pt";
        float actual = CssUtils.parseAbsoluteLength(value, CommonCssConstants.PT);
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAboluteLengthExponential01() {
        String value = "1e2pt";
        float actual = CssUtils.parseAbsoluteLength(value);
        float expected = 1e2f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAboluteLengthExponential02() {
        String value = "1e2px";
        float actual = CssUtils.parseAbsoluteLength(value);
        float expected = 1e2f * 0.75f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 1)})
    public void parseAbsoluteLengthFromUnknownType() {
        String value = "10pateekes";
        float actual = CssUtils.parseAbsoluteLength(value, "pateekes");
        float expected = 10.0f;

        Assert.assertEquals(expected, actual, 0);
    }

    @Test
    public void validateMetricValue() {
        Assert.assertTrue(CssUtils.isMetricValue("1px"));
        Assert.assertTrue(CssUtils.isMetricValue("1in"));
        Assert.assertTrue(CssUtils.isMetricValue("1cm"));
        Assert.assertTrue(CssUtils.isMetricValue("1mm"));
        Assert.assertTrue(CssUtils.isMetricValue("1pc"));
        Assert.assertFalse(CssUtils.isMetricValue("1em"));
        Assert.assertFalse(CssUtils.isMetricValue("1rem"));
        Assert.assertFalse(CssUtils.isMetricValue("1ex"));
        Assert.assertTrue(CssUtils.isMetricValue("1pt"));
        Assert.assertFalse(CssUtils.isMetricValue("1inch"));
        Assert.assertFalse(CssUtils.isMetricValue("+1m"));
    }

    @Test
    public void validateNumericValue() {
        Assert.assertTrue(CssUtils.isNumericValue("1"));
        Assert.assertTrue(CssUtils.isNumericValue("12"));
        Assert.assertTrue(CssUtils.isNumericValue("1.2"));
        Assert.assertTrue(CssUtils.isNumericValue(".12"));
        Assert.assertFalse(CssUtils.isNumericValue("12f"));
        Assert.assertFalse(CssUtils.isNumericValue("f1.2"));
        Assert.assertFalse(CssUtils.isNumericValue(".12f"));
    }

    @Test
    public void parseLength() {
        Assert.assertEquals(9, CssUtils.parseAbsoluteLength("12"), 0);
        Assert.assertEquals(576, CssUtils.parseAbsoluteLength("8inch"), 0);
        Assert.assertEquals(576, CssUtils.parseAbsoluteLength("8", CommonCssConstants.IN), 0);
    }

    @Test
    public void normalizeProperty() {
        Assert.assertEquals("part1 part2", CssUtils.normalizeCssProperty("   part1   part2  "));
        Assert.assertEquals("\" the next quote is ESCAPED \\\\\\\" still  IN string \"", CssUtils.normalizeCssProperty("\" the next quote is ESCAPED \\\\\\\" still  IN string \""));
        Assert.assertEquals("\" the next quote is NOT ESCAPED \\\\\" not in the string", CssUtils.normalizeCssProperty("\" the next quote is NOT ESCAPED \\\\\" NOT in   THE string"));
        Assert.assertEquals("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\"", CssUtils.normalizeCssProperty("\" You CAN put 'Single  Quotes' in double quotes WITHOUT escaping\""));
        Assert.assertEquals("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'", CssUtils.normalizeCssProperty("' You CAN put \"DOUBLE  Quotes\" in double quotes WITHOUT escaping'"));
        Assert.assertEquals("\" ( BLA \" attr(href)\" BLA )  \"", CssUtils.normalizeCssProperty("\" ( BLA \"      AttR( Href  )\" BLA )  \""));
        Assert.assertEquals("\" (  \"attr(href) \"  )  \"", CssUtils.normalizeCssProperty("\" (  \"aTTr( hREf  )   \"  )  \""));
        Assert.assertEquals("rgba(255,255,255,0.2)", CssUtils.normalizeCssProperty("rgba(  255,  255 ,  255 ,0.2   )"));
    }

    @Test
    public void normalizeUrlTest() {
        Assert.assertEquals("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)",
                CssUtils.normalizeCssProperty("url(data:application/font-woff;base64,2CBPCRXmgywtV1t4oWwjBju0kqkvfhPs0cYdMgFtDSY5uL7MIGT5wiGs078HrvBHekp0Yf=)"));
        Assert.assertEquals("url(\"quoted  Url\")", CssUtils.normalizeCssProperty("  url(  \"quoted  Url\")"));
        Assert.assertEquals("url('quoted  Url')", CssUtils.normalizeCssProperty("  url(  'quoted  Url')"));
        Assert.assertEquals("url(haveEscapedEndBracket\\))", CssUtils.normalizeCssProperty("url(  haveEscapedEndBracket\\) )"));
    }

    @Test
    public void parseUnicodeRangeTest() {
        Assert.assertEquals("[(0; 1048575)]", CssUtils.parseUnicodeRange("U+?????").toString());
        Assert.assertEquals("[(38; 38)]", CssUtils.parseUnicodeRange("U+26").toString());
        Assert.assertEquals("[(0; 127)]", CssUtils.parseUnicodeRange(" U+0-7F").toString());
        Assert.assertEquals("[(37; 255)]", CssUtils.parseUnicodeRange("U+0025-00FF").toString());
        Assert.assertEquals("[(1024; 1279)]", CssUtils.parseUnicodeRange("U+4??").toString());
        Assert.assertEquals("[(262224; 327519)]", CssUtils.parseUnicodeRange("U+4??5?").toString());
        Assert.assertEquals("[(37; 255), (1024; 1279)]", CssUtils.parseUnicodeRange("U+0025-00FF, U+4??").toString());

        Assert.assertNull(CssUtils.parseUnicodeRange("U+??????")); // more than 5 question marks are not allowed
        Assert.assertNull(CssUtils.parseUnicodeRange("UU+7-10")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7?-9?")); // wrong syntax
        Assert.assertNull(CssUtils.parseUnicodeRange("U+7-")); // wrong syntax
    }

    @Test
    public void parseAbsoluteFontSizeTest() {
        Assert.assertEquals(75, CssUtils.parseAbsoluteFontSize("100", CommonCssConstants.PX), EPS);
        Assert.assertEquals(75, CssUtils.parseAbsoluteFontSize("100px"), EPS);
        Assert.assertEquals(12, CssUtils.parseAbsoluteFontSize(CommonCssConstants.MEDIUM), EPS);
        Assert.assertEquals(0, CssUtils.parseAbsoluteFontSize("", ""), EPS);
    }

    @Test
    public void parseRelativeFontSizeTest() {
        Assert.assertEquals(120, CssUtils.parseRelativeFontSize("10em", 12), EPS);
        Assert.assertEquals(12.5f, CssUtils.parseRelativeFontSize(CommonCssConstants.SMALLER, 15), EPS);
    }


    @Test
    public void parseAbsoluteLengthTest() {
        Assert.assertEquals(75, CssUtils.parseAbsoluteLength("100", CommonCssConstants.PX), EPS);
        Assert.assertEquals(75, CssUtils.parseAbsoluteLength("100px"), EPS);
    }

    @Test
    public void parseInvalidFloat() {
        String value = "invalidFloat";
        try {
            Assert.assertNull(CssUtils.parseFloat(value));
        } catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void parseAbsoluteLength12cmTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix.
        Assert.assertEquals(340.15747f, CssUtils.parseAbsoluteLength("12cm"), 0f);
    }


    @Test
    public void parseAbsoluteLength12qTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix
        Assert.assertEquals(8.503937f, CssUtils.parseAbsoluteLength("12q"), 0f);
    }

    @Test
    public void testIsAngleCorrectValues() {
        Assert.assertTrue(CssUtils.isAngleValue("10deg"));
        Assert.assertTrue(CssUtils.isAngleValue("-20grad"));
        Assert.assertTrue(CssUtils.isAngleValue("30.5rad"));
        Assert.assertTrue(CssUtils.isAngleValue("0rad"));
    }

    @Test
    public void testIsAngleNullValue() {
        Assert.assertFalse(CssUtils.isAngleValue(null));
    }

    @Test
    public void testIsAngleIncorrectValues() {
        Assert.assertFalse(CssUtils.isAngleValue("deg"));
        Assert.assertFalse(CssUtils.isAngleValue("-20,6grad"));
        Assert.assertFalse(CssUtils.isAngleValue("0"));
        Assert.assertFalse(CssUtils.isAngleValue("10in"));
        Assert.assertFalse(CssUtils.isAngleValue("10px"));
    }

    @Test
    public void parseResolutionValidDpiUnit() {
        Assert.assertEquals(10f, CssUtils.parseResolution("10dpi"), 0);
    }

    @Test
    public void parseResolutionValidDpcmUnit() {
        Assert.assertEquals(25.4f, CssUtils.parseResolution("10dpcm"), 0);
    }

    @Test
    public void parseResolutionValidDppxUnit() {
        Assert.assertEquals(960f, CssUtils.parseResolution("10dppx"), 0);
    }

    @Test
    public void parseResolutionInvalidUnit() {
        Exception e = Assert.assertThrows(StyledXMLParserException.class,
                () -> CssUtils.parseResolution("10incorrectUnit")
        );
        Assert.assertEquals(LogMessageConstant.INCORRECT_RESOLUTION_UNIT_VALUE, e.getMessage());
    }

    @Test
    public void elementNodeIsStyleSheetLink() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertTrue(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeIsNotLink() {
        Element element = new Element(Tag.valueOf("p"), "");
        element.attr(CommonAttributeConstants.REL, CommonAttributeConstants.STYLESHEET);
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    public void elementNodeAttributeIsNotStylesheet() {
        Element element = new Element(Tag.valueOf("link"), "");
        element.attr(CommonAttributeConstants.REL, "");
        JsoupElementNode elementNode = new JsoupElementNode(element);

        Assert.assertFalse(CssUtils.isStyleSheetLink(elementNode));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.INCORRECT_CHARACTER_SEQUENCE))
    public void splitStringWithCommaTest() {
        Assert.assertEquals(new ArrayList<String>(), CssUtils.splitStringWithComma(null));
        Assert.assertEquals(Arrays.asList("value1", "value2", "value3"),
                CssUtils.splitStringWithComma("value1,value2,value3"));
        Assert.assertEquals(Arrays.asList("value1", " value2", " value3"),
                CssUtils.splitStringWithComma("value1, value2, value3"));
        Assert.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with,comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "(val(ue,with,comma),value3"),
                CssUtils.splitStringWithComma("value1,(val(ue,with,comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "(value,with)", "comma)", "value3"),
                CssUtils.splitStringWithComma("value1,(value,with),comma),value3"));
        Assert.assertEquals(Arrays.asList("value1", "( v2,v3)", "(v4, v5)", "value3"),
                CssUtils.splitStringWithComma("value1,( v2,v3),(v4, v5),value3"));
        Assert.assertEquals(Arrays.asList("v.al*ue1\"", "( v2,v3)", "\"(v4,v5;);", "value3"),
                CssUtils.splitStringWithComma("v.al*ue1\",( v2,v3),\"(v4,v5;);,value3"));
    }

    @Test
    public void splitStringTest() {
        Assert.assertEquals(new ArrayList<String>(), CssUtils.splitString(null, ','));
        Assert.assertEquals(Arrays.asList("value1", "(value,with,comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("value1 ", " (val(ue,with,comma),value3"),
                CssUtils.splitString("value1 , (val(ue,with,comma),value3", ',', new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("some text", " (some", " text in", " brackets)", " \"some, text, in quotes,\""),
                CssUtils.splitString("some text, (some, text in, brackets), \"some, text, in quotes,\"", ',',
                        new EscapeGroup('\"')));
        Assert.assertEquals(Arrays.asList("some text", " (some. text in. brackets)", " \"some. text. in quotes.\""),
                CssUtils.splitString("some text. (some. text in. brackets). \"some. text. in quotes.\"", '.',
                        new EscapeGroup('\"'), new EscapeGroup('(', ')')));
        Assert.assertEquals(Arrays.asList("value1", "(value", "with" ,"comma)", "value3"),
                CssUtils.splitString("value1,(value,with,comma),value3", ','));
        Assert.assertEquals(Arrays.asList("value1", "value", "with" ,"comma", "value3"),
                CssUtils.splitString("value1,value,with,comma,value3", ',', new EscapeGroup(',')));
    }

    @Test
    public void parseBlendModeTest() {
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(null));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode(CommonCssConstants.NORMAL));
        Assert.assertEquals(BlendMode.MULTIPLY, CssUtils.parseBlendMode(CommonCssConstants.MULTIPLY));
        Assert.assertEquals(BlendMode.SCREEN, CssUtils.parseBlendMode(CommonCssConstants.SCREEN));
        Assert.assertEquals(BlendMode.OVERLAY, CssUtils.parseBlendMode(CommonCssConstants.OVERLAY));
        Assert.assertEquals(BlendMode.DARKEN, CssUtils.parseBlendMode(CommonCssConstants.DARKEN));
        Assert.assertEquals(BlendMode.LIGHTEN, CssUtils.parseBlendMode(CommonCssConstants.LIGHTEN));
        Assert.assertEquals(BlendMode.COLOR_DODGE, CssUtils.parseBlendMode(CommonCssConstants.COLOR_DODGE));
        Assert.assertEquals(BlendMode.COLOR_BURN, CssUtils.parseBlendMode(CommonCssConstants.COLOR_BURN));
        Assert.assertEquals(BlendMode.HARD_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.HARD_LIGHT));
        Assert.assertEquals(BlendMode.SOFT_LIGHT, CssUtils.parseBlendMode(CommonCssConstants.SOFT_LIGHT));
        Assert.assertEquals(BlendMode.DIFFERENCE, CssUtils.parseBlendMode(CommonCssConstants.DIFFERENCE));
        Assert.assertEquals(BlendMode.EXCLUSION, CssUtils.parseBlendMode(CommonCssConstants.EXCLUSION));
        Assert.assertEquals(BlendMode.HUE, CssUtils.parseBlendMode(CommonCssConstants.HUE));
        Assert.assertEquals(BlendMode.SATURATION, CssUtils.parseBlendMode(CommonCssConstants.SATURATION));
        Assert.assertEquals(BlendMode.COLOR, CssUtils.parseBlendMode(CommonCssConstants.COLOR));
        Assert.assertEquals(BlendMode.LUMINOSITY, CssUtils.parseBlendMode(CommonCssConstants.LUMINOSITY));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("invalid"));
        Assert.assertEquals(BlendMode.NORMAL, CssUtils.parseBlendMode("SCREEN"));
    }

    @Test
    public void isNegativeValueTest() {
        // Invalid values
        Assert.assertFalse(CssUtils.isNegativeValue(null));
        Assert.assertFalse(CssUtils.isNegativeValue("-..23"));
        Assert.assertFalse(CssUtils.isNegativeValue("12 34"));
        Assert.assertFalse(CssUtils.isNegativeValue("12reeem"));

        // Valid not negative values
        Assert.assertFalse(CssUtils.isNegativeValue(".23"));
        Assert.assertFalse(CssUtils.isNegativeValue("+123"));
        Assert.assertFalse(CssUtils.isNegativeValue("57%"));
        Assert.assertFalse(CssUtils.isNegativeValue("3.7em"));

        // Valid negative values
        Assert.assertTrue(CssUtils.isNegativeValue("-1.7rem"));
        Assert.assertTrue(CssUtils.isNegativeValue("-43.56%"));
        Assert.assertTrue(CssUtils.isNegativeValue("-12"));
        Assert.assertTrue(CssUtils.isNegativeValue("-0.123"));
        Assert.assertTrue(CssUtils.isNegativeValue("-.34"));
    }

    @Test
    public void testWrongAttrTest01() {
        String strToParse = "attr((href))";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testWrongAttrTest02() {
        String strToParse = "attr('href')";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testWrongAttrTest03() {
        String strToParse = "attrrname)";
        String result = CssUtils.extractAttributeValue(strToParse, null);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest01() {
        IElementNode iNode = new CssPseudoElementNode(null, "url");
        String strToParse = "attr(url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest02() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest03() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(url url,#one)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("#one", result);
    }

    @Test
    public void testExtractingAttrTest04() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr()";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest05() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr('\')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void testExtractingAttrTest06() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str,\"hey\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("hey", result);
    }

    @Test
    public void testExtractingAttrTest07() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("", result);
    }

    @Test
    public void testExtractingAttrTest08() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("value", result);
    }

    @Test
    public void testExtractingAttrTest09() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,\"val,ue\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest10() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str string,'val,ue')";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertEquals("val,ue", result);
    }

    @Test
    public void testExtractingAttrTest11() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(name, \"value\", \"value\", \"value\")";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void wrongAttributeTypeTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }

    @Test
    public void wrongParamsInAttrFunctionTest() {
        IElementNode iNode = new CssPseudoElementNode(null, "test");
        String strToParse = "attr(str mem lol)";
        String result = CssUtils.extractAttributeValue(strToParse, iNode);
        Assert.assertNull(result);
    }
}
