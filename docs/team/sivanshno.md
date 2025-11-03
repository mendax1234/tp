# Sivansh Nochur - Project Portfolio Page

## Overview

ModHero is a CLI-first desktop app for planning your university degree, designed to give you the speed and precision of
command-line input while retaining the clarity of a graphical overview. ModHero helps you build and adapt your 4-year
course roadmap more efficiently than traditional spreadsheet or browser-based tools.

### Summary of Contributions

- Code contributed
    - Link to
      RepoSense [here](https://nus-cs2113-ay2526s1.github.io/tp-dashboard/?search=sivanshno&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2025-09-19T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)
  - Made an algorithm to make a directed graph of the prerequisites of all the modules, and
    perform a topological sort to auto generate a schedule for the user, and automatically any
    missing prerequisites to the timetable on their behalf. It was not implemented in the final product
    due potential bugs that could appear during the pe dry run.
- Test code contributed
    - `DeleteCommandTest.java`
- Enhancements implemented:
    - Created ModuleNotFound exception to be thrown in different contexts when searching a module 
    - Made the basic major requirements load with their prerequisites as well
    - Implemented **Prerequisite Check** for `delete` command.
- Contributions to UG
  - Section on Add command
  - Fixed documentation issues from the dry run
- Contributions to DG
  - Higher level architecture diagram
  - UI section with diagrams
  - Delete Command Section with diagrams
  - Fixed issues from the dry run
- Contribution to team-based tasks
  - Came up with the list of intial features and specifications for the product
  - Came up with the rough list of objects to be coded for the project
- Review/mentoring contributions
  - Reviewed and gave comments over call for PRs
  - Helped find the link and documentation for the nusmods api
  - taught my teammate how to use postman to make api calls
- Contribution beyond the project team   
  -Reported bugs during the PE dry run. Link to repo with bugs as issues: [link](https://github.com/nus-cs2113-AY2526S1/ped-sivanshno/issues)