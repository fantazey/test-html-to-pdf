package ru.fantazey.generatepdf;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.util.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final ResourceLoader resourceLoader;

    private byte[] getImageAsBytes() throws IOException {
        Resource imageResource = resourceLoader.getResource("classpath:images/beaver.jpeg");
        InputStream is = imageResource.getInputStream();
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        return is.readAllBytes();
    }

    public byte[] pdf1(String text1, String text2) throws DocumentException, IOException {
        BaseFont ptRegBaseFont = BaseFont.createFont("fonts/pt-root-ui_regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font ptf = new Font(ptRegBaseFont, 12, Font.NORMAL);
        BaseFont ptBoldBaseFont = BaseFont.createFont("fonts/pt-root-ui_bold.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font ptbf = new Font(ptBoldBaseFont, 12, Font.BOLD);
        Document d = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(d, baos);
        d.open();
        ptbf.setColor(BaseColor.RED);
        Chunk chunk = new Chunk("ПДФ из кода", ptbf);
        d.add(chunk);
        d.add(Chunk.NEWLINE);
        d.add(Chunk.NEWLINE);
        ptf.setColor(BaseColor.GREEN);
        Paragraph paragraph = new Paragraph();
        chunk = new Chunk("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", ptf);
        paragraph.add(chunk);
        d.add(paragraph);
        ptf.setColor(BaseColor.BLACK);
        chunk = new Chunk(text1, ptf);
        d.add(chunk);
        d.add(Chunk.NEXTPAGE);
        ptbf.setColor(BaseColor.DARK_GRAY);
        chunk = new Chunk(text2, ptbf);
        d.add(chunk);
        d.add(Chunk.NEWLINE);
        byte[] imageBytes = getImageAsBytes();
        Image img = Image.getInstance(imageBytes);
        d.add(img);
        d.add(Chunk.NEXTPAGE);
        chunk = new Chunk("Поисковое задание (ПЗ)", ptbf);
        paragraph = new Paragraph(chunk);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        d.add(paragraph);
        d.close();
        return baos.toByteArray();
    }

    @SneakyThrows
    public String html2(String text1, String text2) {
        ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
        classLoaderTemplateResolver.setSuffix(".html");
        classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
        classLoaderTemplateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine te = new TemplateEngine();
        te.setTemplateResolver(classLoaderTemplateResolver);
        te.setLinkBuilder(new CustomLinkBuilder());

        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("text1", text1);
        thymeleafContext.setVariable("text2", text2);
        thymeleafContext.setVariable("image", "classpath:/images/beaver.jpeg");
        byte[] imageBytes = getImageAsBytes();
        String image = MessageFormat.format("data:image/jpeg;base64,{0}", Base64.getEncoder().encodeToString(imageBytes));
        thymeleafContext.setVariable("imageBase64", image);
        String html = te.process("templates/thymeleaf_pdf_template.html", thymeleafContext);
        return html;
    }

    public static class CustomLinkBuilder extends StandardLinkBuilder {
        @Override
        protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
            if (context instanceof IWebContext) {
                return super.computeContextPath(context, base, parameters);
            }
            return "";
        }
    }

    public byte[] pdf2(String text1, String text2) throws com.lowagie.text.DocumentException, IOException {
        String html = html2(text1, text2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        ClassPathResource fontBold = new ClassPathResource("fonts/pt-root-ui_bold.ttf");
        ClassPathResource fontRegular = new ClassPathResource("fonts/pt-root-ui_regular.ttf");
        renderer.getFontResolver().addFont(fontBold.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont(fontRegular.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        String baseUrl = FileSystems.getDefault()
//                .getPath("src/main/resources/")
//                .toUri().toURL().toString();
        String baseUrl = "http://localhost:8080";
        renderer.setDocumentFromString(html, baseUrl);

        renderer.layout();
        renderer.createPDF(baos);
        renderer.finishPDF();
        return baos.toByteArray();
    }

    @SneakyThrows
    public String html3(String text1, String text2) {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile("templates/mustache_pdf_template.mustache");
        Map<String, String> data = new HashMap<>();
        data.put("text1", text1);
        data.put("text2", text2);
        data.put("imageBase64", MessageFormat.format("data:image/jpeg;base64,{0}", Base64.getEncoder().encodeToString(getImageAsBytes())));
        data.put("imagePath", "/images/beaver.jpeg");
        StringWriter sw = new StringWriter();
        m.execute(sw, data).flush();
        String html = sw.toString();
        return html;
    }

    public byte[] pdf3(String text1, String text2) throws com.lowagie.text.DocumentException, IOException {
        String html = html3(text1, text2);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
//        Configuration.hasValue()
        ClassPathResource fontBold = new ClassPathResource("fonts/pt-root-ui_bold.ttf");
        ClassPathResource fontRegular = new ClassPathResource("fonts/pt-root-ui_regular.ttf");
        renderer.getFontResolver().addFont(fontBold.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont(fontRegular.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        UserAgentCallback userAgentCallback = new ITextUserAgent();
//        StyleReference styleReference = new StyleReference(UserAgentCallback);
//        renderer.getSharedContext().setCss();
        renderer.setDocumentFromString(html, "http://localhost:8080");

        renderer.layout();
        renderer.createPDF(baos);
        renderer.finishPDF();
        return baos.toByteArray();
    }
}
