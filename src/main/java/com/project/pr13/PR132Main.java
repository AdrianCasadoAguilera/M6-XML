package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PR132Main {

    private final Path xmlFilePath;

    public PR132Main(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public static void main(String[] args) {

    }

    public List<List<String>> llistarCursos() {
        List<List<String>> cursos = new ArrayList<>();
        try {
            Document document = carregarDocumentXML();
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList cursNodes = (NodeList) xPath.evaluate("/cursos/curs", document, XPathConstants.NODESET);

            for (int i = 0; i < cursNodes.getLength(); i++) {
                Element curs = (Element) cursNodes.item(i);
                String idCurs = curs.getAttribute("id");
                String tutor = xPath.evaluate("tutor", curs);
                NodeList alumnesNodes = (NodeList) xPath.evaluate("alumnes/alumne", curs, XPathConstants.NODESET);
                int totalAlumnes = alumnesNodes.getLength();

                List<String> cursInfo = new ArrayList<>();
                cursInfo.add(idCurs);
                cursInfo.add(tutor);
                cursInfo.add(String.valueOf(totalAlumnes));
                cursos.add(cursInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos;
    }

    public List<List<String>> mostrarModuls(String idCurs) {
        List<List<String>> moduls = new ArrayList<>();
        try {
            Document document = carregarDocumentXML();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("/cursos/curs[@id='" + idCurs + "']/moduls/modul");
            NodeList modulNodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < modulNodes.getLength(); i++) {
                Element modul = (Element) modulNodes.item(i);
                String idModul = modul.getAttribute("id");
                String titol = modul.getElementsByTagName("titol").item(0).getTextContent();

                List<String> modulInfo = new ArrayList<>();
                modulInfo.add(idModul);
                modulInfo.add(titol);
                moduls.add(modulInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moduls;
    }

    public List<String> llistarAlumnes(String idCurs) {
        List<String> alumnes = new ArrayList<>();
        try {
            Document document = carregarDocumentXML();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("/cursos/curs[@id='" + idCurs + "']/alumnes/alumne");
            NodeList alumneNodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < alumneNodes.getLength(); i++) {
                alumnes.add(alumneNodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alumnes;
    }

    public void afegirAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("/cursos/curs[@id='" + idCurs + "']/alumnes");
            Element alumnesElement = (Element) expr.evaluate(document, XPathConstants.NODE);

            Element nouAlumne = document.createElement("alumne");
            nouAlumne.setTextContent(nomAlumne);
            alumnesElement.appendChild(nouAlumne);

            guardarDocumentXML(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML();
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("/cursos/curs[@id='" + idCurs + "']/alumnes/alumne[text()='" + nomAlumne + "']");
            Element alumneElement = (Element) expr.evaluate(document, XPathConstants.NODE);

            if (alumneElement != null) {
                alumneElement.getParentNode().removeChild(alumneElement);
                guardarDocumentXML(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document carregarDocumentXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlFilePath.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error en carregar el document XML.", e);
        }
    }

    private void guardarDocumentXML(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFilePath.toFile());
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException("Error en guardar el document XML.", e);
        }
    }
}
