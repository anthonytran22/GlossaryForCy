import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Easy to maintain glossary facility.
 *
 * @author Anthony Tran
 *
 */
public final class GlossaryFacility {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private GlossaryFacility() {
    }

    /**
     * Parses a text input to extract terms and their corresponding definitions,
     * stores them in a Map and returns a Queue of the terms in alphabetical
     * order.
     *
     * @param input
     *            the text input to be parsed
     * @param termAndDef
     *            a Map where the keys are terms and the values are their
     *            definitions
     * @return a Queue of the terms in alphabetical order
     * @requires input != null && termAndDef != null
     * @ensures the contents of input have been parsed and added to termAndDef,
     *          and a Queue of the terms in alphabetical order has been returned
     */
    public static Queue<String> termsAndDefinitions(SimpleReader input,
            Map<String, String> termAndDef) {
        Queue<String> terms = new Queue1L<>();
        /*
         * While loop to go through entire input.
         *
         */
        while (!input.atEOS()) {
            String next = input.nextLine();
            String definition = "";
            String term = "";
            boolean emptyLine = true;

            /*
             * Empty line stops the loop, otherwise the next word will be put
             * into term
             */
            if (next.equals("")) {

                emptyLine = false;
            } else {

                term = next;
            }

            while (emptyLine && !input.atEOS()) {
                /*
                 * Words will be given a definition until an empty line or end
                 * of file
                 *
                 */
                next = input.nextLine();
                if (!next.equals("")) {
                    definition = definition + " " + next;
                } else {
                    emptyLine = false;
                }
            }
            /*
             * Adds the term and its corresponding definition
             */
            termAndDef.add(term, definition.toString());
            terms.enqueue(term);
        }
        // Sort the terms queue alphabetically by iterating through the queue
        int n = terms.length();
        String[] termsArray = new String[n];
        for (int i = 0; i < n; i++) {
            termsArray[i] = terms.dequeue();
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (termsArray[j].compareTo(termsArray[i]) < 0) {
                    String temp = termsArray[i];
                    termsArray[i] = termsArray[j];
                    termsArray[j] = temp;
                }
            }
        }
        for (int i = 0; i < n; i++) {
            terms.enqueue(termsArray[i]);
        }
        return terms;

    }

    /**
     * Updates the definitions in a Map of terms and definitions to include HTML
     * hyperlinks to the corresponding term pages.
     *
     * @param terms
     *            a Queue of the terms in alphabetical order
     * @param termAndDef
     *            a Map where the keys are terms and the values are their
     *            definitions
     * @requires terms != null && termAndDef != null
     * @ensures the definitions in termAndDef have been updated with HTML
     *          hyperlinks to the corresponding term pages
     */
    public static void fetchDefinition(Queue<String> terms,
            Map<String, String> termAndDef) {
        /* Set of separators used to split the definitions into words. */
        Set<Character> wordSeparators = new Set1L<>();
        wordSeparators.add(',');
        wordSeparators.add(' ');
        wordSeparators.add('\t');
        wordSeparators.add(';');
        wordSeparators.add('.');

        /*
         * If a term is found, the definition is updated to contain an HTML
         * hyperlink to the corresponding term page.
         */
        for (int i = 0; i < terms.length(); i++) {
            String currentTerm = terms.front();
            for (int j = 0; j < terms.length(); j++) {
                String otherTerm = terms.front();
                String definition = termAndDef.value(currentTerm);
                int position = 0;
                while (position < definition.length()) {
                    String currentWord = getNextWordOrSeparator(definition,
                            position, wordSeparators);
                    if (currentWord.equals(otherTerm)) {
                        definition = definition.substring(0, position)
                                + "<a href=\"" + otherTerm + ".html\">"
                                + otherTerm + "</a>"
                                + definition.substring(
                                        position + otherTerm.length(),
                                        definition.length());
                    }
                    position += currentWord.length();
                }
                termAndDef.replaceValue(currentTerm, definition);
                terms.rotate(1);
            }
            terms.rotate(1);
        }
    }

    /**
     * Generates the title page for the glossary.
     *
     * @param writer
     *            the SimpleWriter used to output the HTML code.
     * @param terms
     *            the Queue of terms to include in the index of the title page.
     *
     * @ensures <pre>
     * The HTML output file generated contains a title,
     * a list of terms with hyperlinks, and an index.
     * </pre>
     */
    public static void generateTitle(SimpleWriter writer, Queue<String> terms) {
        writer.println("<html>");
        writer.println("  <head>");
        writer.println("      <title>Glossary</title>");
        writer.println("  </head>");
        writer.println("  <body>");
        writer.println("      <h2>Glossary</h2>");
        writer.println("      <hr>");
        writer.println("      <h3>Index</h3>");
        writer.println("      <ul>");
        /* For loop to add a hyperlink for each term's page. */
        for (int i = 0; i < terms.length(); i++) {
            writer.println("          <li>");
            writer.println("              <a href=\"" + terms.front()
                    + ".html\">" + terms.front() + "</a>");
            writer.println("          </li>");
            terms.rotate(1);
        }

        writer.println("      </ul>");
        writer.println("  </body>");
        writer.println("</html>");
    }

    /**
     * Generates an HTML page for a given term and its definition.
     *
     * @param outputFile
     *            the SimpleWriter object to write the generated HTML code to
     * @param term
     *            the term to generate a page for
     * @param termsAndDef
     *            a Map containing terms and their definitions
     * @requires outputFile and term are not null
     * @requires termsAndDef is not null and contains the definition for the
     *           given term
     * @ensures an HTML page is generated for the given term and its definition
     */
    public static void generateTermPage(SimpleWriter outputFile, String term,
            Map<String, String> termsAndDef) {
        outputFile.println("<html>");
        outputFile.println("  <head>");
        outputFile.println("      <title>" + term + "</title>");
        outputFile.println("  </head>");
        outputFile.println("  <body>");
        outputFile.println("<h2><b><i><font color=\"red\">" + term
                + "</font></i></b></h2>");
        /*
         * Prints to outputFile the term and its definition in html
         */
        outputFile.println(
                "<blockquote>" + termsAndDef.value(term) + "</blockquote>");
        outputFile.println("      <hr>");
        outputFile
                .println("<p>Return to <a href=\"index.html\">index</a>.</p>");
        outputFile.println("  </body>");
        outputFile.println("</html>");
    }

    /**
     *
     * Returns the next word or separator in a given text starting at a given
     * position. A separator is any character in the given set of separators.
     *
     * @param text
     *            the text to search for the next word or separator
     * @param position
     *            the starting position in the text to search
     * @param separatorSet
     *            the set of characters to consider as separators
     * @return the next word or separator in the text
     * @requires text is not null and 0 <= position < |text|
     * @ensures <pre>
     If a separator is returned, it is contained in the given set of separators.
     If a word is returned, the word does not contain any separator from the given set.
     </pre>
     */
    public static String getNextWordOrSeparator(String text, int position,
            Set<Character> separatorSet) {
        assert text != null : "Violation of: text is not null";
        assert separatorSet != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String wordOrSeparator = "";
        if (separatorSet.contains(text.charAt(position))) {
            for (int i = position; i < text.length()
                    && separatorSet.contains(text.charAt(i)); i++) {
                wordOrSeparator += text.charAt(i);
            }
        } else {
            for (int i = position; i < text.length()
                    && !separatorSet.contains(text.charAt(i)); i++) {
                wordOrSeparator += text.charAt(i);
            }
        }
        return wordOrSeparator;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.print("What is the file name? ");
        String inputFile = in.nextLine();
        SimpleReader inpFile = new SimpleReader1L(inputFile);
        out.print("Where would you like to save these files? ");
        String outputFold = in.nextLine();

        Map<String, String> termsAndDef = new Map1L<>();
        Queue<String> terms = termsAndDefinitions(inpFile, termsAndDef);
        fetchDefinition(terms, termsAndDef);

        SimpleWriter out2 = new SimpleWriter1L(outputFold + "/index.html");
        generateTitle(out2, terms);

        int length = terms.length();
        for (int h = 0; h < length; h++) {
            String term = terms.dequeue();
            out2 = new SimpleWriter1L(outputFold + "/" + term + ".html");
            generateTermPage(out2, term, termsAndDef);
        }

        out2.close();
        in.close();
        out.close();
    }

}
