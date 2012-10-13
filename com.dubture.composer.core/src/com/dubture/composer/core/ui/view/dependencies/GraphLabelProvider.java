package com.dubture.composer.core.ui.view.dependencies;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.viewers.EntityConnectionData;
import org.eclipse.zest.core.viewers.IEntityStyleProvider;

import com.dubture.composer.core.build.InstalledPackage;

public class GraphLabelProvider extends LabelProvider implements IEntityStyleProvider
{
    private Color LIGHT_BLUE = new Color(Display.getDefault(), 216, 228, 248);
    private Color DARK_BLUE = new Color(Display.getDefault(), 1, 70, 122);
    private Color LIGHT_GREY = new Color(null, 192, 192, 192);

    @Override
    public String getText(Object element)
    {
        if (element instanceof InstalledPackage) {
            return ((InstalledPackage) element).getFullName();
        }

        if (element instanceof EntityConnectionData) {
            return "";
        }

        return super.getText(element);
    }

    @Override
    public Color getNodeHighlightColor(Object entity)
    {
        return ColorConstants.yellow;
    }

    @Override
    public Color getBorderColor(Object entity)
    {
        return LIGHT_GREY;
    }

    @Override
    public Color getBorderHighlightColor(Object entity)
    {
        return LIGHT_GREY;
    }

    @Override
    public int getBorderWidth(Object entity)
    {
        return 0;
    }

    @Override
    public Color getBackgroundColour(Object entity)
    {
        if (entity instanceof InstalledPackage && ((InstalledPackage)entity).isDev) {
            return ColorConstants.lightGray;
        }
        return LIGHT_BLUE;
    }

    @Override
    public Color getForegroundColour(Object entity)
    {
        return DARK_BLUE;
    }

    @Override
    public IFigure getTooltip(Object entity)
    {
        return null;
    }

    @Override
    public boolean fisheyeNode(Object entity)
    {
        return false;
    }
}
