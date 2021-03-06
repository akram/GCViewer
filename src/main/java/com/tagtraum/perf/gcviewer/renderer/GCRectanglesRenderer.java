package com.tagtraum.perf.gcviewer.renderer;

import com.tagtraum.perf.gcviewer.ChartRenderer;
import com.tagtraum.perf.gcviewer.ModelChartImpl;
import com.tagtraum.perf.gcviewer.model.AbstractGCEvent;
import com.tagtraum.perf.gcviewer.model.GCEvent;

import java.awt.*;
import java.util.Iterator;

/**
 * GCRectanglesRenderer.
 * <p/>
 * Date: Jun 2, 2005
 * Time: 3:31:21 PM
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class GCRectanglesRenderer extends ChartRenderer {
    public static final Paint DEFAULT_LINEPAINT = Color.GRAY;
    private Paint darker;
    private Paint brighter;

    public GCRectanglesRenderer(ModelChartImpl modelChart) {
        super(modelChart);
        setLinePaint(DEFAULT_LINEPAINT);
    }

    public void setLinePaint(Paint linePaint) {
        super.setLinePaint(linePaint);
        if (linePaint instanceof Color) {
            darker = ((Color) linePaint).darker();
            brighter = ((Color) linePaint).brighter();
        } else {
            // TODO add fancy logic for GradientPaint etc...
            darker = linePaint;
            brighter = linePaint;
        }
    }

    public void paintComponent(Graphics2D g2d) {
        // make sure that we ignore the AntiAliasing flag as it does not make sense for vertical lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        final double scaleFactor = getModelChart().getScaleFactor();
        final double scaledHeight = (getHeight() / getModelChart().getMaxPause());

        int lastWidth = Integer.MIN_VALUE;
        int lastHeight = Integer.MIN_VALUE;
        int lastX = Integer.MIN_VALUE;
        int lastY = Integer.MIN_VALUE;

        for (Iterator i = getModelChart().getModel().getGCEvents(); i.hasNext();) {
            GCEvent event = (GCEvent) i.next();
            final double pause = event.getPause();
            final int width = (int) Math.max(Math.abs(scaleFactor * pause), 1.0d);
            final int height = (int) (pause * scaledHeight);
            final int x = (int) (scaleFactor * event.getTimestamp());
            final int y = getHeight() - (int) (pause * scaledHeight);
            if (lastX != x || lastY != y || lastWidth != width || lastHeight != height) {
                if (event.getType() == AbstractGCEvent.Type.FULL_GC) {
                    g2d.setPaint(darker);
                } else if (event.getType() == AbstractGCEvent.Type.INC_GC) {
                    g2d.setPaint(brighter);
                } else {
                    g2d.setPaint(getLinePaint());
                }
                g2d.fillRect(x, y, width, height);
                lastWidth = width;
                lastHeight = height;
                lastX = x;
                lastY = y;
            }
        }
    }


}
