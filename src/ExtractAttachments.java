import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.stream.IntStream;

public class ExtractAttachments {

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args[0] == null || args[0].length() == 0) {
            System.err.println("No UBL file specified");
            System.exit(1);
        }

        File ubl = new File(args[0]);
        Path dir = ubl.toPath().toAbsolutePath().getParent();

        XPathExpression xpath = XPathFactory.newInstance().newXPath()
                .compile("//*[local-name()='EmbeddedDocumentBinaryObject']");
        NodeList list = (NodeList) xpath.evaluate(
                new InputSource(new FileInputStream(ubl)),
                XPathConstants.NODESET);

        System.out.printf("About to extract %d attachments in %s.%n", list.getLength(), dir);
        IntStream.range(0, list.getLength()).forEach(i -> {
            Node node = list.item(i);
            String filename = node.getAttributes().getNamedItem("filename").getTextContent();
            byte[] content = Base64.getDecoder().decode(node.getTextContent());
            try {
                System.out.printf("Extracting attachment %d: %s (%.2f MB)... ",
                        i + 1, filename, (double) content.length / 1048576);
                Files.write(dir.resolve(filename), content, StandardOpenOption.CREATE_NEW);
                System.out.println("extracted.");
            } catch (FileAlreadyExistsException e) {
                System.out.println("skipped, file exists.");
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
        System.out.println("Finished.");
    }

}
