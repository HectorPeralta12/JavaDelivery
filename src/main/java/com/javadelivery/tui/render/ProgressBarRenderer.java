package com.javadelivery.tui.render;

/**
 * Renders ASCII progress bars.
 */
public class ProgressBarRenderer {
    
    private static final char FILLED = '█';
    private static final char EMPTY = '░';

    /**
     * Renders a progress bar with the given percentage and width.
     * 
     * @param percent Progress percentage (0-100)
     * @param width Total width of the bar in characters
     * @return ASCII progress bar string
     */
    public static String render(int percent, int width) {
        int filled = (int) Math.round((percent / 100.0) * width);
        filled = Math.max(0, Math.min(width, filled));
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            sb.append(i < filled ? FILLED : EMPTY);
        }
        return sb.toString();
    }

    /**
     * Renders a progress bar with default width of 6.
     */
    public static String render(int percent) {
        return render(percent, 6);
    }
}
