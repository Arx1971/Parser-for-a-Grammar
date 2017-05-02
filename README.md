# Parser-for-a-Grammar
Developed a parser/lexical analyzer in java to assess string entered for a particular grammar.

## Grammar Tested
1. `<program> -> begin <statement_list> end`
2. `<statement_list> -> <statement> {;<statement_list>}`
3. `<statement> -> <assignment_statement> | <loop_statement>`
4. `<assignment_statement> -> <variable> = <expression>`
5. `<variable> -> identifier  (An identifier is a string that begins with a letter followed by 0 or more letters and/or digits)`
6. `<expression> -> <variable> { (+|-) <variable>}`           
7. `<loop_statement> -> loop (<logic_expression>)  <statement>`
8. `<logic_expression> -> <variable> (< | >) <variable>  (Assume that logic expressions have only less than or greater than operators)`
