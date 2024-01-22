package be.vlaanderen.vip.magda.client;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

class MagdaDocumentTest {

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
    void setValueOrRemoveNode_setsValueIfSpecified() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>Donald</bar>
                    <baz>Trump</baz>
                </foo>""");

        magdaDocument.setValueOrRemoveNode("//foo/bar", "Stormy Daniels");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>Stormy Daniels</bar>
                    <baz>Trump</baz>
                </foo>
                """).ignoreWhitespace());
    }

    @Test
    void setValueOrRemoveNode_removesNodeIfValueNotSpecified() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>Donald</bar>
                    <baz>Trump</baz>
                </foo>""");

        magdaDocument.setValueOrRemoveNode("//foo/bar", null);

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <baz>Trump</baz>
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
    void getValues_getsAllMatchedValues() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz>trump</baz>
                </foo>""");

        assertEquals(List.of("donald", "john"), magdaDocument.getValues("//foo/bar"));
    }

    @Test
    void getValues_returnEmptyListIfThereIsNoMatch() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz>trump</baz>
                </foo>""");

        assertEquals(List.of(), magdaDocument.getValues("//foo/qux"));
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

    @Test
    void createAttribute_createsAttribute() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.createAttribute("//bar", "att", "J");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar att="J">donald</bar>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void removeAttribute_removesAttribute() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar att="J">donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeAttribute("//bar/@att");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void removeAttribute_removesAttributesForAllMatches() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar att="J">donald</bar>
                    <bar att="D">john</bar>
                    <baz att="Z">trump</baz>
                </foo>""");

        magdaDocument.removeAttribute("//bar/@att");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <bar>john</bar>
                    <baz att="Z">trump</baz>
                </foo>""").ignoreWhitespace());
    }

    @Test
    void removeAttribute_doesNothingIfThereIsNoSuchAttribute() {
        var magdaDocument = MagdaDocument.fromString("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""");

        magdaDocument.removeAttribute("//bar/@att");

        assertThat(magdaDocument.toString(), isIdenticalTo("""
                <foo>
                    <bar>donald</bar>
                    <baz>trump</baz>
                </foo>""").ignoreWhitespace());
    }
}
