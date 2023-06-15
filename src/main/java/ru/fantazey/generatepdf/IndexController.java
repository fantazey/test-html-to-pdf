package ru.fantazey.generatepdf;

import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(value = "index")
@AllArgsConstructor
public class IndexController {

    private PdfService pdfService;


    @GetMapping(value = "pdf1", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ResponseEntity<byte[]> getPdf1(@RequestParam("text1") String text1, @RequestParam("text2") String text2) {
        return ResponseEntity.ok(pdfService.pdf1(text1, text2));
    }

    @GetMapping(value = "pdf2", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ResponseEntity<byte[]> getPdf2(@RequestParam("text1") String text1, @RequestParam("text2") String text2) {
        return ResponseEntity.ok(pdfService.pdf2(text1, text2));
    }

    @GetMapping(value = "html2", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ResponseEntity<byte[]> getHtml2(@RequestParam("text1") String text1, @RequestParam("text2") String text2) {
        return ResponseEntity.ok(pdfService.html2(text1, text2).getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "html3", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ResponseEntity<byte[]> getHtml3(@RequestParam("text1") String text1, @RequestParam("text2") String text2) {
        return ResponseEntity.ok(pdfService.html3(text1, text2).getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping(value = "pdf3", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public ResponseEntity<byte[]> getPdf3(@RequestParam("text1") String text1, @RequestParam("text2") String text2) {
        return ResponseEntity.ok(pdfService.pdf3(text1, text2));
    }
}
