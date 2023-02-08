package be.vlaanderen.vip.magda.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MagdaDocumentTest {

    @Test
    void setValue_setsValue() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.setValue("//foo/bar", "judd");

        assertEquals("""
                <foo>
                    <bar>judd</bar>
                    <baz>trump</baz>
                </foo>""",
                magdaDocument.toString());
    }

    @Test
    void setValue_setsValuesForMultipleMatches() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.setValue("//foo/bar", "judd");

        assertEquals("""
                <foo>
                    <bar>judd</bar>
                    <bar>judd</bar>
                    <baz>trump</baz>
                </foo>""",
                magdaDocument.toString());
    }

    @Test
    void setValue_doesNothingIfThereIsNoSuchField() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.setValue("//foo/qux", "judd");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""",
                magdaDocument.toString());
    }

    @Test
    void getValue_readsValue() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        assertEquals("donald", magdaDocument.getValue("//foo/bar"));
    }

    @Test
    void getValue_readsValueFromFirstMatch() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz>trump</baz>
                </foo>""");

        assertEquals("donald", magdaDocument.getValue("//foo/bar"));
    }

    @Test
    void getValue_returnsNullIfThereIsNoSuchField() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        assertNull(magdaDocument.getValue("//foo/qux"));
    }

    @Test
    void createNode_createsNode() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.createNode("//foo", "qux");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                <qux/></foo>""",
                magdaDocument.toString());
    }

    @Test
    void createNode_addsNodeEvenIfAnotherExists() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                </foo>""");

        magdaDocument.createNode("//foo", "qux");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                <qux/></foo>""",
                magdaDocument.toString());
    }

    @Test
    void createTextNode_createsNodeWithTextValue() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.createTextNode("//foo", "qux", "john");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                <qux>john</qux></foo>""",
                magdaDocument.toString());
    }

    @Test
    void createTextNode_addsNodeWithTextValueEvenIfAnotherNodeExists() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                </foo>""");

        magdaDocument.createTextNode("//foo", "qux", "rich");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                <qux>rich</qux></foo>""",
                magdaDocument.toString());
    }

    @Test
    void removeNode_removesNode() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeNode("//foo/bar");

        assertEquals(
                "<foo>\n" +
                "    \n" +
                "    <baz>trump</baz>\n" +
                "</foo>",
                magdaDocument.toString());
    }

    @Test
    void removeNode_removesNodesForAllMatches() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeNode("//foo/bar");

        assertEquals(
                "<foo>\n" +
                        "    \n" +
                        "    \n" +
                        "    <baz>trump</baz>\n" +
                        "</foo>",
                magdaDocument.toString());
    }

    @Test
    void removeNode_doesNothingIfThereIsNoSuchNode() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeNode("//foo/qux");

        assertEquals("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""",
                magdaDocument.toString());
    }
}
