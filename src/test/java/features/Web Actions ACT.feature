Feature: Web Actions ACT1
#Regression Type
#Correct Values = true
#Incorrect Values = false
#Illegal Values = false
#Invalid Values = false
#Boundary Values = false
#Edge Cases Values = false

@The_Internet_1
@uida1982752441
@set21
@test001
@Form_Authentication
Scenario Outline: Form Authentication
Given I have access to application
When I scroll and click Form Auth link in the internet
And I entered Username_TB in form authentication as '<Username_TB1>'
And I clear text Username_TB in form authentication
And I copied text Login page_TB in form authentication
And I enter copied text Username_TB in form authentication
And I clear and enter text Username_TB in form authentication as '<Username_TB2>'
And I clear text Username_TB in form authentication
And I copied text usename head_link in form authentication
And I enter copied text Username_TB in form authentication
And I copied text password head_label in form authentication
And I enter copied text Username_TB in form authentication
And I entered Password_TA in form authentication as '<Password_TA>'
And I clear text Password_TA in form authentication
And I entered Password_TA in form authentication as '<Password_TA3>'
And I clear and enter text Password_TA in form authentication as '<Password_TA4>'
And I selected Login_button in form authentication
Then '<page>' is displayed with '<content>'

Examples:
|SlNo.|Username_TB1|Username_TB2|Password_TA|Password_TA3|Password_TA4|page|content|
|1|Username_TB1|Username_TB2|Password_TA1|Password_TA2|Password_TA3|NA|NA|

#Total No. of Test Cases : 1

