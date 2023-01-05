package be.vlaanderen.vip.mock.magda.client;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import lombok.SneakyThrows;

public class SimulatorXmlValidation {
    
    // some xml files we don't want to validate
    private final List<String> EXCLUSIONS = Arrays.asList(
            "Vastgoed\\GeefEpc\\02.01.0000\\9470\\Leeuwbrugstraat\\21.xml",                 // contains SOAP-ENV:Fault, which is not covered by the xsd
            "Persoon\\GeefAanslagbiljetPersonenbelasting\\02.00.0000\\70021500155.xml"      // contains SOAP-ENV:Fault, which is not covered by the xsd
            );

    private String baseXsd = "simulator_xsd\\";
    private String baseXml = "magda_simulator\\";

    private Map<String, String> XML_FOLDERS_AND_XSDS = data(
            // folder inside of magdasimulator                          path to xsd file inside of simuator xsd
            // that will be validated against                           the format inside that folder is expected to be the same as
            // xsds on the right                                        in https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/487620609/Overzicht+testdata+endpoints+en+XSD+s#Diensten-alle-versies-samen
            "GeefVipAdmGegevens\\02.00.0000",                           "VipAdm.GeefVipAdmGegevensDienst-02.00\\WebService\\GeefVipAdmGegevensResponse.xsd",
            "Onderneming\\GeefOnderneming\\02.00.0000",                 "Onderneming.GeefOndernemingDienst-02.00\\WebService\\GeefOndernemingResponse.xsd",
            "Persoon\\GeefAanslagbiljetPersonenbelasting\\02.00.0000",  "Inkomen.GeefAanslagbiljetPersonenbelastingDienst-02.00\\WebService\\GeefAanslagbiljetPersonenbelastingResponse.xsd",
            "Persoon\\GeefAttest\\02.00.0000",                          "Persoon.GeefAttestDienst-02.00\\WebService\\GeefAttestResponse.xsd",
            "Persoon\\GeefBewijs\\02.00.0000",                          "LED.GeefBewijsDienst-02.00\\WebService\\GeefBewijsResponse.xsd",
            "Persoon\\GeefDossierKBI\\01.00.0000",                      "Inburgering.GeefDossierKBIDienst-01.00\\WebService\\GeefDossierKBIResponse.xsd",
            "Persoon\\GeefDossiers\\02.00.0000",                        "Dossier.GeefDossiersDienst-02.00\\WebService\\GeefDossiersResponse.xsd",
            "Persoon\\GeefFuncties\\02.00.0000",                        "Onderneming.GeefFunctiesDienst-02.00\\WebService\\GeefFunctiesResponse.xsd",
            "Persoon\\GeefGezinssamenstelling\\02.00.0000",             "Persoon.GeefGezinssamenstellingDienst-02.00\\WebService\\GeefGezinssamenstellingResponse.xsd",
            "Persoon\\GeefGezinssamenstelling\\02.02.0000",             "Persoon.GeefGezinssamenstellingDienst-02.02\\WebService\\GeefGezinssamenstellingResponse.xsd",
            "Persoon\\GeefHistoriekInschrijving\\02.01.0000",           "Onderwijs.GeefHistoriekInschrijvingDienst-02.01\\WebService\\GeefHistoriekInschrijvingResponse.xsd",
            "Persoon\\GeefHistoriekPersoon\\02.00.0000",                "Persoon.GeefHistoriekPersoonDienst-02.00\\WebService\\GeefHistoriekPersoonResponse.xsd",
            "Persoon\\GeefHistoriekPersoon\\02.02.0000",                "Persoon.GeefHistoriekPersoonDienst-02.02\\WebService\\GeefHistoriekPersoonResponse.xsd",
            "Persoon\\GeefKindVoordelen\\02.00.0000",                   "Gezin.GeefKindVoordelenDienst-02.00\\WebService\\GeefKindVoordelenResponse.xsd",
            "Persoon\\GeefLoopbaanonderbrekingen\\02.00.0000",          "Werk.GeefLoopbaanonderbrekingenDienst-02.00\\WebService\\GeefLoopbaanonderbrekingenResponse.xsd",
            "Persoon\\GeefPasfoto\\02.00.0000",                         "Persoon.GeefPasfotoDienst-02.00\\WebService\\GeefPasfotoResponse.xsd",
            "Persoon\\GeefPersoon\\02.02.0000",                         "Persoon.GeefPersoonDienst-02.02\\WebService\\GeefPersoonResponse.xsd",
            "Persoon\\GeefVolledigDossierHandicap\\03.00.0000",         "SocZek.GeefVolledigDossierHandicapDienst-03.00\\WebService\\GeefVolledigDossierHandicapResponse.xsd",
            "Persoon\\RegistreerInschrijving\\02.00.0000",              "Repertorium.RegistreerInschrijvingDienst-02.00\\WebService\\RegistreerInschrijvingResponse.xsd",
            "Persoon\\RegistreerInschrijving\\02.01.0000",              "Repertorium.RegistreerInschrijvingDienst-02.01\\WebService\\RegistreerInschrijvingResponse.xsd",
            "Persoon\\RegistreerUitschrijving\\02.00.0000",             "Repertorium.RegistreerUitschrijvingDienst-02.00\\WebService\\RegistreerUitschrijvingResponse.xsd",
            "Persoon\\ZoekEigendomstoestanden\\02.00.0000",             "Kadaster.ZoekEigendomstoestandenDienst-02.00\\WebService\\ZoekEigendomstoestandenResponse.xsd",
            "Persoon\\ZoekPersoonOpAdres\\02.02.0000",                  "Persoon.ZoekPersoonOpAdresDienst-02.02\\WebService\\ZoekPersoonOpAdresResponse.xsd",
            "Vastgoed\\GeefEpc\\02.01.0000",                            "Energie.GeefEpcDienst-02.01\\WebService\\GeefEpcResponse.xsd",
            "Onderneming\\GeefOndernemingVKBO\\02.00.0000",             "Onderneming.GeefOndernemingVKBODienst-02.00\\WebService\\GeefOndernemingVKBOResponse.xsd",
            "Persoon\\GeefStatusRechtOndersteuningen\\02.00.0000",      "SocEcon.GeefStatusRechtOndersteuningenDienst-02.00\\WebService\\GeefStatusRechtOndersteuningenResponse.xsd");

    
    @TestFactory
    Stream<DynamicNode> validateXmls() {
        return XML_FOLDERS_AND_XSDS.entrySet()
                                   .stream()
                                   .map(e -> forXsd(e.getKey(), e.getValue()));
    }
    
    DynamicContainer forXsd(String xmlFolder, String xsd) {
        var validator = getValidator(testFile(baseXsd + xsd));
        return dynamicContainer(xmlFolder, 
                                findXmls(mainFile(baseXml + xmlFolder))
                                    .map(xml -> forXml(xml, validator)));
    }
    
    DynamicTest forXml(File xml, Validator validator) {
        return dynamicTest(xml.getName(), () -> {
            validateXml(xml, validator);
        });
    }
    
    @SneakyThrows
    private Stream<File> findXmls(File file) {
        if(file.isDirectory()) {
            return Arrays.stream(file.listFiles())
                         .flatMap(this::findXmls);
        }
        else if(isXml(file) && isNotExcluded(file)) {
            return Stream.of(file);
        }
        return Stream.empty();
    }
    
    private boolean isXml(File file) {
        return file.getName().endsWith("xml");
    }
    
    private boolean isNotExcluded(File file) {
        return EXCLUSIONS.stream()
                         .noneMatch(ex -> file.getPath().endsWith(ex));
    }
    
    private static Map<String, String> data(String...data) {
        return IntStream.iterate(0, i -> i + 2)
                        .takeWhile(i -> i < data.length)
                        .collect(HashMap::new, 
                                 (m, i) -> m.put(data[i], data[i+1]),
                                 HashMap::putAll);
    }
    
    void validateXml(File xml, Validator validator) {
        try {
            validator.validate(new StreamSource(xml));
        } catch (Exception e) {
            fail("Validation failure for %s: %s".formatted(xml.getName(), e.getMessage()));
        }
    }

    private Validator getValidator(File xsd) {
        try {
            var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = factory.newSchema(xsd);
            var validator =  schema.newValidator();
            validator.setErrorHandler(new XsdErrorHandler());
            return validator;
        } catch (Exception e) {
            fail("Cannot create xml validator for %s: %s".formatted(xsd.getName(), e.getMessage()));
            return null;
        }
    }
    
    private File mainFile(String path) {
        return new File("src/main/resources/" + path);
    }
    
    private File testFile(String path) {
        return new File("src/test/resources/" + path);
    }
    
    public class XsdErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) throws SAXException { }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            handleMessage(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            handleMessage(exception);
        }

        private String handleMessage(SAXParseException e) throws SAXException {
            throw new SAXException("Error occurred on line %s: %s".formatted(e.getLineNumber(), e.getMessage()));
        }
    }
    
}
