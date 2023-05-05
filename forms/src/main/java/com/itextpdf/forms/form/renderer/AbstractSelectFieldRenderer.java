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
package com.itextpdf.forms.form.renderer;

import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.AbstractSelectField;
import com.itextpdf.forms.form.element.IFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link BlockRenderer} for select form fields.
 */
public abstract class AbstractSelectFieldRenderer extends BlockRenderer {

    /**
     * Creates a new {@link AbstractSelectFieldRenderer} instance.
     *
     * @param modelElement the model element
     */
    protected AbstractSelectFieldRenderer(AbstractSelectField modelElement) {
        super(modelElement);
        addChild(createFlatRenderer());
        if (!isFlatten()) {
            // TODO DEVSIX-1901
            Logger logger = LoggerFactory.getLogger(AbstractSelectFieldRenderer.class);
            logger.warn(FormsLogMessageConstants.ACROFORM_NOT_SUPPORTED_FOR_SELECT);
            setProperty(FormProperty.FORM_FIELD_FLATTEN, Boolean.TRUE);
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        // Resolve width here in case it's relative, while parent width is still intact.
        // If it's inline-block context, relative width is already resolved.
        Float width = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
        if (width != null) {
            updateWidth(UnitValue.createPointValue((float)width));
        }

        float childrenMaxWidth = getMinMaxWidth().getMaxWidth();

        LayoutArea area = layoutContext.getArea().clone();
        area.getBBox().moveDown(INF - area.getBBox().getHeight()).setHeight(INF).setWidth(childrenMaxWidth + EPS);
        LayoutResult layoutResult = super.layout(new LayoutContext(area, layoutContext.getMarginsCollapseInfo(),
                layoutContext.getFloatRendererAreas(), layoutContext.isClippedHeight()));

        if (layoutResult.getStatus() != LayoutResult.FULL) {
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                layoutResult = makeLayoutResultFull(layoutContext.getArea(), layoutResult);
            } else {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
            }
        }

        float availableHeight = layoutContext.getArea().getBBox().getHeight();
        boolean isClippedHeight = layoutContext.isClippedHeight();

        Rectangle dummy = new Rectangle(0, 0);
        applyMargins(dummy, true);
        applyBorderBox(dummy, true);
        applyPaddings(dummy, true);
        float additionalHeight = dummy.getHeight();

        availableHeight -= additionalHeight;
        availableHeight = Math.max(availableHeight, 0);
        float actualHeight = getOccupiedArea().getBBox().getHeight() - additionalHeight;

        float finalSelectFieldHeight = getFinalSelectFieldHeight(availableHeight, actualHeight, isClippedHeight);
        if (finalSelectFieldHeight < 0) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
        }

        float delta = finalSelectFieldHeight - actualHeight;
        if (Math.abs(delta) > EPS) {
            getOccupiedArea().getBBox().increaseHeight(delta).moveDown(delta);
        }

        return layoutResult;
    }


    @Override
    public void drawChildren(DrawContext drawContext) {
        if (isFlatten()) {
            super.drawChildren(drawContext);
        } else {
            applyAcroField(drawContext);
        }
    }

    /**
     * Gets the accessibility language.
     *
     * @return the accessibility language
     */
    protected String getLang() {
        return this.<String>getProperty(FormProperty.FORM_ACCESSIBILITY_LANGUAGE);
    }

    protected abstract IRenderer createFlatRenderer();

    protected abstract void applyAcroField(DrawContext drawContext);

    /**
     * Checks if form fields need to be flattened.
     *
     * @return true, if fields need to be flattened
     */
    protected boolean isFlatten() {
        return (boolean) getPropertyAsBoolean(FormProperty.FORM_FIELD_FLATTEN);
    }

    /**
     * Gets the model id.
     *
     * @return the model id
     */
    protected String getModelId() {
        return ((IFormField) getModelElement()).getId();
    }

    protected float getFinalSelectFieldHeight(float availableHeight, float actualHeight, boolean isClippedHeight) {
        boolean isForcedPlacement = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT));
        if (!isClippedHeight && actualHeight > availableHeight) {
            if (isForcedPlacement) {
                return availableHeight;
            }
            return -1;
        }
        return actualHeight;
    }

    protected List<IRenderer> getOptionsMarkedSelected(IRenderer optionsSubTree) {
        List<IRenderer> selectedOptions = new ArrayList<>();
        for (IRenderer option : optionsSubTree.getChildRenderers()) {
            if (isOptionRenderer(option)) {
                if (Boolean.TRUE.equals(option.<Boolean>getProperty(FormProperty.FORM_FIELD_SELECTED))) {
                    selectedOptions.add(option);
                }
            } else {
                List<IRenderer> subSelectedOptions = getOptionsMarkedSelected(option);
                selectedOptions.addAll(subSelectedOptions);
            }
        }
        return selectedOptions;
    }

    private LayoutResult makeLayoutResultFull(LayoutArea layoutArea, LayoutResult layoutResult) {
        IRenderer splitRenderer = layoutResult.getSplitRenderer() == null ? this : layoutResult.getSplitRenderer();
        if (occupiedArea == null) {
            occupiedArea = new LayoutArea(layoutArea.getPageNumber(),
                    new Rectangle(layoutArea.getBBox().getLeft(), layoutArea.getBBox().getTop(), 0, 0));
        }
        layoutResult = new LayoutResult(LayoutResult.FULL, occupiedArea, splitRenderer, null);
        return layoutResult;
    }

    static boolean isOptGroupRenderer(IRenderer renderer) {
        return renderer.hasProperty(FormProperty.FORM_FIELD_LABEL) &&
                !renderer.hasProperty(FormProperty.FORM_FIELD_SELECTED);
    }

    static boolean isOptionRenderer(IRenderer child) {
        return child.hasProperty(FormProperty.FORM_FIELD_SELECTED);
    }
}
