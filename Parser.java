/**Name: Joseph Tassone
 * Description: Implementation of  a lexical and syntax analyzer based on a small grammar.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {

    public static ArrayList <Character> lexeme = new ArrayList();   // holds the completed tokens temporarily
    public static String lexTemp = "";                              // used to determine between identifiers and keywords
    public static char nextChar;                                    // the temporary variable for each character read in
    public static Tokens nextToken;                                 // the token code of the next lexeme
    public static CharacterClasses charClass;                       // the character class: letter, number, or unknown
    public static BufferedReader buffer;                            // the buffer for going through the file

    public static void main(String [] args) throws IOException {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter the file location: ");
        String file = input.next();

        buffer = new BufferedReader(new FileReader(file));
        program();
        System.out.println("There were no syntax errors!");
    }

    //First initiated part of the parser
    //Grammar: begin <statement_list> end
    public static void program() throws IOException {
        getChar();
        lex();
        if(nextToken != Tokens.BEGIN) {
            System.out.println("Error!");
            System.exit(0);
        }

        //The program must not be EOF or END, right after BEGIN
        lex();
        if(nextToken == Tokens.END || nextToken == Tokens.EOF) {
            System.out.println("Error!");
            System.exit(0);
        }

        //Checks if it's a valid statement_list
        if(statement_list() == false) {
            System.out.println("Error!");
            System.exit(0);
        }

        //There's an error if it doesn't have "end" as the last part of the program
        if(nextToken != Tokens.END) {
            System.out.println("Error!");
            System.exit(0);
        }
    }

    //Grammar: <statement> {;<statement_list>}
    public static boolean statement_list() throws IOException {
        if(statement() == false) {
            return false;
        }
        if (nextToken == Tokens.SEMICOLON) {
            lex();
            if(nextToken == Tokens.END) {
                return false;
            }
            else {
                return statement_list();
            }
        }
        return true;
    }

    //Grammar: <assignment_statement> | <loop_statement>
    public static boolean statement() throws IOException {
        if(assignment_statement() == true) {
            return true;
        }
        else if(loop_statement() == true) {
            return true;
        }
        return false;
    }

    //Grammar: <variable> = <expression>
    public static boolean assignment_statement() throws IOException {
        if(variable() == true) {
            lex();
            if(nextToken == Tokens.ASSIGN_OP) {
                lex();
                if(expression() == true) {
                    return true;
                }
            }
        }
        return false;
    }

    //Grammar: identifier
    public static boolean variable() throws IOException {
        if(nextToken == Tokens.IDENT) {
            return true;
        }
        return false;
    }

    //Grammar: <variable> { (+|-) <variable>}
    public static boolean expression() throws IOException {
        if(variable() == true) {
            lex();
            while((nextToken == Tokens.ADD_OP) || (nextToken == Tokens.SUB_OP)) {
                lex();
                if(variable() == true) {
                    lex();
                }
            }
            return true;
        }
        return false;
    }

    //Grammar: loop (<logic_expression>) <statement>
    public static boolean loop_statement() throws IOException {
        if(nextToken == Tokens.LOOP) {
            lex();
            if(nextToken == Tokens.LEFT_PAREN) {
                lex();
                if(logic_expression() == true) {
                    if(nextToken == Tokens.RIGHT_PAREN) {
                        lex();
                        if(statement() == true) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //Grammar: <variable> (< | >) <variable>
    public static boolean logic_expression() throws IOException {
        if(variable() == true) {
            lex();
            while((nextToken == Tokens.GREATER) || (nextToken == Tokens.LESS)) {
                lex();
                if(variable() == true) {
                    lex();
                    return true;
                }
            }
        }
        return false;
    }

    //lookup table used to separate "UNKNOWNS" into tokens
    //returns the type of UNKNOWN
    public static Tokens lookup(char ch) {
        switch(ch) {
            case '(':
                addChar();
                nextToken = Tokens.LEFT_PAREN;
                break;

            case ')':
                addChar();
                nextToken = Tokens.RIGHT_PAREN;
                break;

            case '+':
                addChar();
                nextToken = Tokens.ADD_OP;
                break;

            case '-':
                addChar();
                nextToken = Tokens.SUB_OP;
                break;

            case '=':
                addChar();
                nextToken = Tokens.ASSIGN_OP;
                break;

            case '>':
                addChar();
                nextToken = Tokens.GREATER;
                break;

            case '<':
                addChar();
                nextToken = Tokens.LESS;
                break;

            case ';':
                addChar();
                nextToken = Tokens.SEMICOLON;
                break;

            default:
                addChar();
                nextToken = Tokens.EOF;
                break;
        }
        return nextToken;
    }

    //adds the individual characters to an arraylist
    public static void addChar() {
        lexTemp = lexTemp + nextChar;
        lexeme.add(nextChar);
    }

    //retrieves and identifies the individual characters for later token separation
    //searches through the file for each individual character
    public static void getChar() throws IOException {
        int single;
        single = buffer.read();
        nextChar = (char) single;

        if((nextChar) != -1) {
            if(Character.isLetter(nextChar)) {
                charClass = CharacterClasses.LETTER;
            }
            else if(Character.isDigit(nextChar)) {
                charClass = CharacterClasses.DIGIT;
            }
            else {
                charClass = CharacterClasses.UNKNOWN;
            }
        }
        else {
            charClass = CharacterClasses.EOF;
        }
    }

    //handles whitespace characters and ignores them
    public static void getNonBlank() throws IOException {
        while(Character.isWhitespace(nextChar)) {
            getChar();
        }
    }

    //assigns the tokens to each identified lexeme and returns the identity of the token
    public static Tokens lex() throws IOException {
        getNonBlank();

        //determines if the token is an Identifier or some type of keyword
        switch(charClass) {
            case LETTER:
                addChar();
                getChar();
                while(charClass == CharacterClasses.LETTER || charClass == CharacterClasses.DIGIT) {
                    addChar();
                    getChar();
                }
                if(lexTemp.equals("begin")) {
                    nextToken = Tokens.BEGIN;
                }
                else if(lexTemp.equals("end")) {
                    nextToken = Tokens.END;
                }
                else if(lexTemp.equals("loop")) {
                    nextToken = Tokens.LOOP;
                }
                else {
                    nextToken = Tokens.IDENT;
                }
                break;

            //determines if the lexeme is a number or an incorrect (garbage) identifier
            case DIGIT:
                addChar();
                getChar();
                while(charClass == CharacterClasses.DIGIT) {
                    addChar();
                    getChar();
                }
                if(charClass == CharacterClasses.LETTER) {
                    nextToken = Tokens.GARBAGE;
                }
                else {
                    nextToken = Tokens.INT_LIT;
                }
                break;

            //checks the lookup table to assign the correct Token for an UNKNOWN
            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;
        }
        lexTemp = "";
        return nextToken;
    }
}
