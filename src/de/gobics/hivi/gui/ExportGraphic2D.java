package de.gobics.hivi.gui;

import de.gobics.marvis.utils.swing.filechooser.ChooserImage;
import de.gobics.marvis.utils.swing.filechooser.FileFilterImageGif;
import de.gobics.marvis.utils.swing.filechooser.FileFilterImageJpg;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;

/**
 * Utility class to export some drawable AWT/Swing component into an SVG file.
 *
 * @author manuel
 */
public class ExportGraphic2D {

    public enum GraphicType {

        JPEG, GIF, PNG
    }

    public static void export(Component drawable) throws IOException {
        ChooserImage chooser = ChooserImage.getInstance();
        File destination = chooser.doChooseFileSave(drawable);
        if (destination == null) {
            return;
        }

        FileFilter ff = chooser.getFileFilter();

        // Raster images
        String type = "png";
        if (ff instanceof FileFilterImageJpg) {
            type = "jpeg";
        } else if (ff instanceof FileFilterImageGif) {
            type = "gif";
        }

        exportRaster(drawable, type, destination);

    }

    public static void exportRaster(Component drawable, String type, File destination) throws IOException {
        BufferedImage image = new BufferedImage(drawable.getSize().width, drawable.getSize().height, BufferedImage.TYPE_INT_ARGB);
        drawable.paint(image.getGraphics());
        ImageIO.write(image, type, destination);
    }
}
