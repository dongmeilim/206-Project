# 206-Project

## Discussion Wiki

### 09/10/2019 @ 1739 - Let the Games Begin

Finally have the TextSelect step (now renamed to Create the Sound) implemented. It has been redone to remove the need to the child user to name their audio files (kids don't have that kind of patience), as well as add the ability to play and delete saved audio files. The audio slider has been removed as it took up too much precious space in the window. The play/pause/stop controls should be enough for the child. The voices have also been given more descriptive(?) names.

Here's how the window looks currently:
![textselect](Concepts/screenshots_PRE_CSS/TextSelect.png)

With this done, more of my focus can be put on the game component now :D

\- dongmeilim

### 08/10/2019 @ 1553 - Rain Cloud Under The Honey Tree

`MirlingtonWindows` was finished sooner than I expected. Here's a look at the Save Video step:

![savevideo](Concepts/screenshots_PRE_CSS/PreviewSave.png)

Here are some other things we agreed on while coding:

1. A loading icon is needed before navigating to the Save Video step.
2. We need to download a blacklist file of inappropriate words so that we can prevent the child from searching up them. This should be done by a simple `for` loop with `BufferedReader`.
3. A `quiz` branch will be made while we work on the quiz together
4. Decided on some fresh icons for the Menu screen, see below:

![menu](Concepts/screenshots_PRE_CSS/Menu.png)

\- Mirlington

### 06/10/2019 @ 2015 - The Branch Less Traveled

Today I branched off from `master` into `MirlingtonWindows`. When I finally dug my hands into some code, I noticed there were things we could improve on from last time:

1. We've proposed to make a `quiz` branch off `master` so upon which we can work on together.
2. Simplified the Controller Class, no more `switchNonSkippableWindow` among other things.
3. We switched to a `750x550` resolution to get a better landscape look and feel.

Other things:

* We agreed on one-liner methods, so long as they're simple and commonly used.

**The Simplified Creation Cycle**

1. Search Word.
2. Create Audio.
3. Fetch Images.
4. Save Video.

Today I was able to get step 3 done, take a look.

![imagefetch](Concepts/screenshots_PRE_CSS/ImageFetch.png)

I can't believe it looks that great and we're still using the old design!

\- Mirlington

---

### 06/10/2019 @ 1034 - Hauling Over and Under

**Introduction**

Before I begin, I'd like to point out that this project builds upon the foundations of our [third assignment](https://github.com/dongmeilim/Assignment_3_206).

Compared to last time we have a [hefty brief](Misc/project_brief.pdf) as we are now developing for a stakeholder - a child user 7-10 years - so along the process of design we will strive to envision their user story.

**Functionality**

Yesterday @dongmeilim and I met up to discuss the functionality, and while we were making decisions, I mocked up sketches in my workbook.

*Some key decisions we made along the way:*

A help button will be on every window for instant convenience, we thought this would appeal to a kid. When clicked, extra information will be overlayed on the current window.

Buttons will always appear in the same place, just like last time.
  * Home Button on top left (Except for Menu)
  * Back Button on bottom left (Where appropriate)
  * Next Button on bottom right (Where appropriate)
  * Help Button on top right (Always)

We received a global [presentation feedback](Misc/PresentationFeedback.md) from our lecturers, from this we decided to implement the following:
  * Have a reset button for wikit text
  * When you pause the video - save the time, when you play the video, load the time
  * Bind buttons to keys for everything
  * Preview video before saving
  * Voice names - more descriptive (kaldiphone means nothing to the user)
  * Warn the user preemptively that the NZ voice cant pronounce a lot of words.

In the [brief](Misc/project_brief.pdf) we chose to create a matching game, to be more appealing to the stakeholder.
  * Options:
    * Audio only
    * Images only
    * Both
  * Searched-term will be redacted from the media during the matching game.
  * The quality of the quiz depends on the quality of the creation, so we wish to discourage the user from making trivial videos, e.g. one-word narrated only.

Like last time we will implement the UI and functionality first before making the app pretty with CSS.

**After the meeting, I took to the computer to make the map**

![intialmap](Concepts/maps/initialmap.png)

We've removed a lot of steps from our previous assignment; we believe that just the core features will appeal to the kid the most.

We have also omitted the video-list and video-playback section of the screendiagram, this is because we intend it to be almost identical, except that this time we wish to show video thumbnails in the video-list.

\- Mirlington
