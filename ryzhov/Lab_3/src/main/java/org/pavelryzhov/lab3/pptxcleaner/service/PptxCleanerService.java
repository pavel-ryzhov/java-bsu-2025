package org.pavelryzhov.lab3.pptxcleaner.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class PptxCleanerService {
    private static final Pattern PATTERN = Pattern.compile("image-\\d{4}-1\\.png");
    private static final String TARGET_DIR = "ppt/media/";
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;

    public record CleanerResult(byte[] data, int cleanedCount) {
    }

    public CleanerResult cleanPresentation(InputStream inputStream, String customText, String colorHex) throws IOException {
        var baos = new ByteArrayOutputStream();
        int counter = 0;
        try (var zis = new ZipInputStream(inputStream);
             var zos = new ZipOutputStream(baos)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                var fullPath = entry.getName();
                var newEntry = new ZipEntry(fullPath);
                zos.putNextEntry(newEntry);
                boolean isInTargetDir = fullPath.startsWith(TARGET_DIR);
                var fileNameOnly = fullPath.substring(fullPath.lastIndexOf('/') + 1);
                if (isInTargetDir && !entry.isDirectory() && PATTERN.matcher(fileNameOnly).matches()) {
                    var image = ImageIO.read(new FilterInputStream(zis) {
                        @Override
                        public void close() {
                        }
                    });
                    if (image != null) {
                        ImageIO.write(editImage(image, customText, colorHex), "png", zos);
                        counter++;
                    } else {
                        zis.transferTo(zos);
                    }
                } else {
                    zis.transferTo(zos);
                }
                zos.closeEntry();
            }
        }
        return new CleanerResult(baos.toByteArray(), counter);
    }

    private BufferedImage editImage(BufferedImage image, String customText, String colorHex) {
        var result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (customText == null || customText.isBlank()) {
            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
        } else {
            int fontSize = Math.max(20, image.getHeight() / 3);
            var font = new Font("Arial", Font.BOLD, fontSize);
            var fm = g.getFontMetrics(font);
            if (fm.stringWidth(customText) > image.getWidth() * 0.9) {
                float ratio = (float) (image.getWidth() * 0.9) / fm.stringWidth(customText);
                font = font.deriveFont(fontSize * ratio);
            }
            var fontRenderContext = g.getFontRenderContext();
            var glyphVector = font.createGlyphVector(fontRenderContext, customText);
            Shape textShape = glyphVector.getOutline();
            var bounds = textShape.getBounds2D();
            double xOffset = (image.getWidth() - bounds.getWidth()) / 2 - bounds.getX();
            double yOffset = (image.getHeight() - bounds.getHeight()) / 2 - bounds.getY();
            var transform = java.awt.geom.AffineTransform.getTranslateInstance(xOffset, yOffset);
            Shape centeredShape = transform.createTransformedShape(textShape);
            Color paintColor;
            try {
                paintColor = (colorHex != null && !colorHex.isBlank()) ? Color.decode(colorHex) : DEFAULT_TEXT_COLOR;
            } catch (NumberFormatException e) {
                paintColor = DEFAULT_TEXT_COLOR;
            }
            g.setColor(paintColor);
            g.fill(centeredShape);
        }
        g.dispose();
        return result;
    }
}
