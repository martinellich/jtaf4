package ch.jtaf.reporting.report;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.lowagie.text.Element.*;
import static com.lowagie.text.FontFactory.HELVETICA;

public abstract class AbstractReport {

    static final float HEADER_FONT = 16f;
    static final float HEADER_FONT_SMALL = 12f;

    static final float DEFAULT_FONT_SIZE = 9f;
    static final float CM_PER_INCH = 2.54f;
    static final float DPI = 72f;

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    int numberOfRows = 0;

    final ResourceBundle messages;

    AbstractReport(Locale locale) {
        this.messages = ResourceBundle.getBundle("messages", locale);
    }

    protected float cmToPixel(float cm) {
        return cm / CM_PER_INCH * DPI;
    }

    protected void addCell(PdfPTable table, String text) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, DEFAULT_FONT_SIZE)));
        cell.setBorder(0);
        table.addCell(cell);
    }

    protected void addCell(PdfPTable table, String text, float fontSize) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, fontSize)));
        cell.setBorder(0);
        table.addCell(cell);
    }

    protected void addCellAlignRight(PdfPTable table, String text) {
        var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, DEFAULT_FONT_SIZE)));
        cell.setBorder(0);
        cell.setHorizontalAlignment(ALIGN_RIGHT);
        table.addCell(cell);
    }

    class HeaderFooter extends PdfPageEventHelper {

        private final PdfPTable header = new PdfPTable(new float[] { 2.5f, 4f, 1f });

        HeaderFooter(String left, String middle, String right) {
            header.setWidthPercentage(100f);
            header.setSpacingBefore(cmToPixel(1f));

            addHeaderCellAlignLeft(header, left);
            addHeaderCellAlignCenter(header, middle);
            addHeaderCellAlignRight(header, right);
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            try {
                document.add(header);
                numberOfRows = 0;
            }
            catch (DocumentException ex) {
                LoggerFactory.getLogger(HeaderFooter.class).error(ex.getMessage(), ex);
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            var table = new PdfPTable(3);
            table.setWidthPercentage(100f);

            var cellLeft = new PdfPCell(new Phrase(DATE_TIME_FORMATTER.format(LocalDate.now()),
                    FontFactory.getFont(HELVETICA, DEFAULT_FONT_SIZE)));
            cellLeft.setBorder(0);
            cellLeft.setBorderWidthTop(1f);
            table.addCell(cellLeft);

            var cellCenter = new PdfPCell(
                    new Phrase("JTAF - Track and Field | www.jtaf.ch | sponsored by Martinelli LLC",
                            FontFactory.getFont(HELVETICA, DEFAULT_FONT_SIZE)));
            cellCenter.setBorder(0);
            cellCenter.setBorderWidthTop(1f);
            cellCenter.setHorizontalAlignment(ALIGN_CENTER);
            table.addCell(cellCenter);

            var cellRight = new PdfPCell(new Phrase("Page %d".formatted(document.getPageNumber()),
                    FontFactory.getFont(HELVETICA, DEFAULT_FONT_SIZE)));
            cellRight.setBorder(0);
            cellRight.setBorderWidthTop(1f);
            cellRight.setHorizontalAlignment(ALIGN_RIGHT);
            table.addCell(cellRight);

            var page = document.getPageSize();
            table.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
            table.writeSelectedRows(0, 1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());
        }

        private void addHeaderCellAlignLeft(PdfPTable table, String text) {
            var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, HEADER_FONT)));
            cell.setBorder(0);
            cell.setVerticalAlignment(ALIGN_BOTTOM);
            table.addCell(cell);
        }

        private void addHeaderCellAlignCenter(PdfPTable table, String text) {
            var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, HEADER_FONT)));
            cell.setBorder(0);
            cell.setHorizontalAlignment(ALIGN_CENTER);
            cell.setVerticalAlignment(ALIGN_BOTTOM);
            table.addCell(cell);
        }

        private void addHeaderCellAlignRight(PdfPTable table, String text) {
            var cell = new PdfPCell(
                    new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, HEADER_FONT_SMALL)));
            cell.setBorder(0);
            cell.setHorizontalAlignment(ALIGN_RIGHT);
            cell.setVerticalAlignment(ALIGN_BOTTOM);
            table.addCell(cell);
        }

    }

}
