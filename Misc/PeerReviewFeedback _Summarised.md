# Summary of Peer Review Feedback

## From Reviewer 1:

### Code Quality

High ratings.

**Comment**

Good use of many small classes to handle individual tasks and minimise code duplication. The clear package structure also gives your project very clean design. The clear package layout and consistent and code convention and comments make the code very readable. One thing that I am missing from the readme is an indication of what platform this application should be run on.

### Tidyness

* Unnecessary files are not deleted.
* Program exits cleanly

**Comment**

After closing the application, there still remains a directory name “tmp” that contains the intermediate files for creating a creation. From my understanding and by the name of the directory, I believe that this directory is meant to be removed after closure of the application.

### Ease of Use

High ratings.

**Comment**

I really appreciate the simplicity of the error messages when the application receives unexpected input. Another standout feature that improves the ease of use is the consistent question mark help function, which is very aesthetically implemented and really benefits the user.

### Look and Feel

High ratings.

**Comment**

I have not seen any indication of what the target user is, however, I am assuming that it is for children. The general look and feel is very refined all around, with fitting colours, suitable and easyto-read font, and quality details, such as shadows and subtle gradients. You have a great looking application!

### Learning Component

No comments due to inability to create creations.

### Broken functionality

I am testing the application on the linux desktop on the ECSE lab computers. The application works flawlessly up until I finish selecting images and click the next arrow. The loading circle fills up and then the tick appears. At this point, the GUI does not progress onto the next scene, and the following error is thrown in the terminal:

`javafx.fxml.LoadException:`
`file:/afs/ec.auckland.ac.nz/users/upi111/unixhome/Downloads/dlim654_bpip078/runnable.jar!/application/view/PreviewSave.fxml`

As I am unable to create a creation, I cannot test the quiz functionality.

### Overall Comment

The application looks like a full product that would be used in the real world. There is exquisite attention to detail and therefore has a very refined appearance. Great work!

---

## From Reviewer 2:

### Code Quality

High ratings.

**Comment**

Overall the code is well organised and easy to understand. I haven’t seen repeated codes and I like your commenting. Each class has a reasonable size and number of lines. I like how you separate helper class, controller and scenes into different folders, that makes the layout clear. 

### Tidyness

Satisfied.

### Ease of Use

Average to high ratings.

**Comment**

For selecting the number of images part, I suggest to use another feature like a choiceBox or something else. Since I am quite confused up to that stage for what I need to do. Especially for the second-language people (is second-language right?), they probably don’t understand what “fetch images” means (since I am a second-language person and I don’t understand it). Try to use simple words, to avoid the possibility that the user don’t understand the instruction. Also the preview part can reduce the number of buttons, since too many buttons can cause confusion. 

### Look and Feel

High ratings.

**Comment**

The colour choice is nice and make eyes feel comfortable. The instruction text is really helpful. Since the creation function does work, I can’t tested the quiz function. But overall, the help function and text instructions are really helpful for second-langauge to follow. I like the Create Sound window part. 

### Learning Component

No comments due to inability to create creations.

### Broken functionality

1. The Create Creation function does not work. After I click the button and the progress bar start to generate, the application stop progressing and nothing happened. There are errors showing up in the command line. As a user I would expect the platform to jump the the mainMenu page for the user to play their creation directly. 
2. After I click the ? button on the mainMenu page and the click ? again, the quiz button is enabled where it shouldn’t since I haven’t create three creations yet. 

### Overall Comment

The “?” button function is really useful for second language people to follow. Since the Create Creation function does not work so many other project requirements cannot be tested. But I believe it’s just a small bug to fix and I can see your quiz structure and how it will function, it’s really nice : D

I really like how you use progress bar for each process generating, which allows the user to understand the application is in progress.  

---

## From Reviewer 3:

### Code Quality

High ratings.

**Comment**

I liked how you made a controller class with the common methods, then extended it to reduce repetition in other controllers. The code was split into Controller, App and View folders which made it easier to understand. The View folders contained the FXML files

### Tidyness

* Unnecessary files are not deleted.
* Program does not exit cleanly

**Comment**

I like the excellent file management not only does the App, Controller and View folders make the code easier to understand but encapsulates it too. I noticed a “tmp” folder that stays after closing. Consider deleting this on exit to keep the file system tidy. The video player stops threads from ending when the program closes (if playing a creation). You can use `Platform.exit` to end running threads. It would be a good idea to call this command when the user clicks the “x” button.

### Ease of Use

Average ratings.

**Comment**

If I type in a term not in Wikipedia, it informs me. However, if I then type another unknown term  straight after, it accepts it. I Couldn’t get it to happen again so am not sure how you would be able to replicate it. Also, when I created a video about New Zealand, the preview video was about “cat” with the displayed word being New Zealand. No matter the search term it is always about cat. I think the program (when it is working normally) overall is intuitive, good error handling and ease of use. But the unexpected behavior is the reason for the lower scores. I also would have liked instructions on how to match on the quiz rather than having to click the “?” button. One small improvement could be to have the default volume of the preview player at 100%. Currently it is 0%.

### Look and Feel

High ratings.

**Comment**

The theme was well thought out for the target audience. Theme looks playful and colourful, perfect for children. This theme was also applied consistently across the scenes

### Learning Component

**Comment**

The idea of allowing audio, video or both helps cater to the different VARK learning styles, arguably essential for effective learning in young people.

Selecting match video (for the quiz) and then selecting next causes the GUI to freeze, I am then presented with 2 blank lists. Perhaps this feature isn’t implemented yet? If it is, it would be a good idea to put whatever is causing the GUI to freeze in a separate thread.

### Broken functionality

There was broken functionality as discussed in previous answers (particularly in Ease of Use section). I am running it on the Beta Linux Labs as specified when asking them in person what to test on. The recommended platform was not specified in the readme file. Due to the complex nature of the cat video issue I am unable to recommend a solution. However, it should be fixed.

### Overall Comment

Special features include the last term searched being displayed under the search bar as a nice reminder and the “?” button that displays help info in a very effective manner. The prototype does not meet the requirements due to the previously discussed broken functionality, particularly the issue with it creating cat videos. The GUI is well suited to the target audience as previously discussed. Very colourful and interesting for children. 

---

## From Reviwer 4:

### Code Quality

High ratings.

**Comment**

The Readme was very helpful on giving a base explanation of what the project was and how to run it. I took off a point because the target audience was not included. I know it didn’t say to include it in the handout provided, but I felt it would have been helpful to know!

### Tidyness

Satisfied.

**Comment**

After closing the application, there still remains a directory name “tmp” that contains the intermediate files for creating a creation. From my understanding and by the name of the directory, I believe that this directory is meant to be removed after closure of the application.

### Ease of Use

High ratings.

**Comment**

The error tolerance was marked down for not including protection against setting flags as the search term (such as “-help” and “-version”). These would pull up the actual help menu or version number for the wikit command, rather than a response from Wikipedia or error message. I’m assuming this was not as intended. The ease of use was only marked down because, while it was easy to use, the application broke after adding pictures to a creation.

### Look and Feel

High ratings.

**Comment**

I thought the look and feel was excellent. The design of the GUI was without a doubt this application’s strongest element.

### Learning Component

No comments due to inability to create creations.

### Broken functionality

There was some broken functionality. I was running the application on a VirtualBox Linux image, with Oracle Java 9 installed. I also tested it on the Beta Linux in UG4. First, and most prominently, nothing after adding pictures to a creation worked, so creations were never created and never able to be viewed in the Videos screen or the Quiz screen. Once the pictures have been selected and you click the “Next” arrow, a check mark appears at the bottom of the screen, but an error appears in the terminal. This is something to do with the PreviewSave.fxml file and the media player not displaying. Because of this, creations don’t ever get saved or show up anywhere in the application. Second was with the “?” button. If you clicked it twice, then it re-enabled elements that were disabled, such as being able to access the Quiz component without having two creations saved.

### Overall Comment

There was no target user included, but I am going to assume that the target user was young children. If this is the case, the GUI does meet the needs because it is fun, colorful, and very easy to use and understand. One special feature added was the “?” buttons throughout the application that made help-text pop up on each screen to explain what each of the elements did. Another was saving the most recent search term, even when the application had been closed entirely. Third was the progress bars showing how far along the Wikit response and Flickr image queries were. These were all really nice and helpful! The prototype does not meet the project requirements because the functionality of making creations (and thus viewing, playing, and reviewing them) is not currently functional. I’m sure this should be able to be fixed before the final submission though.

I don’t think there is any intentionally missing features, but the fact that making creations doesn’t work past adding pictures (explained above) means that the Videos and Quiz screen are never actually utilized.

I really liked the look and feel of the GUI and the way the making of creations guided the user through the process, whether by breaking up each of the sections on different screens or providing the “Help” button to explain what buttons and elements did. Once the error with making creations is fixed, I think this will be a really nice application!
