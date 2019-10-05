# 206-Project

## Discussion Wiki

---

### 06/10/2019 @ XXXX - Hauling Over and Under

**Introduction**

Before I begin, I'd like to point out that this project builds upon the foundations of our [third assignment](https://github.com/dongmeilim/Assignment_3_206).

Compared to last time we have a [hefty brief](project_brief.pdf) as we are now developing for a stakeholder - a child user 7-10 years - so along the process of design we will strive to envision their user story.

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
