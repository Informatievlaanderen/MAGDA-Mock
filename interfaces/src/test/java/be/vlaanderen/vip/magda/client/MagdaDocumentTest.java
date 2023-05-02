package be.vlaanderen.vip.magda.client;

import org.junit.jupiter.api.Test;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>judd</bar>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>judd</bar>
                    <bar>judd</bar>
                    <baz>trump</baz>
                </foo>
                """).ignoreWhitespace());
    }

    @Test
    void setValue_doesNothingIfThereIsNoSuchField() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.setValue("//foo/qux", "judd");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>
                """).ignoreWhitespace());
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux/>
                </foo>""").ignoreWhitespace());
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                    <qux/>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void createTextNode_createsNodeWithTextValue() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.createTextNode("//foo", "qux", "john");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                </foo>""").ignoreWhitespace());
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                    <qux>john</qux>
                    <qux>rich</qux>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void removeNode_removesNode() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeNode("//foo/bar");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
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

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void removeNode_doesNothingIfThereIsNoSuchNode() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeNode("//foo/qux");


        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
    }
}
