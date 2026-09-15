# Lab Reflection — WalkMates

**Lab:** 1
**Pair:** Simon Kalitta (sika2400), Kiryl Yasny (kiya2400)
**Repo commit/tag:** [https://github.com/SimonKalitta/walkmates-test/commits/main/](https://github.com/SimonKalitta/walkmates-test/commits/main/), commits `8820add` to `c0f1e5b`.

---

### 1. What we did

We conducted quality attribute analysis (ISO/IEC 25010), an error -> fault -> failure breakdown and produced Equivalence Partitioning (EP), Boundary Value Analysis (BVA) and a Decision Table. We validated user input validation for registration (FR-1.1) and analyzed legal state machine transitions for listings (FR-3.2) and LLM prompt encapsulation to prevent injection (FR-5.2). We implemented an example of a unit test for each of the analyzed requirements.

### 2. What we found

One of the most notable finding was a fault in `BookingService::createBooking` regarding seeker booking limits. The system evaluated `>` instead of `>=`. Another finding was that the `Seeker::PHONE` pattern requires 7 digits after `+467`, which results in a total of 11 characters. This should be 12 characters:

```java
private static final Pattern PHONE = Pattern.compile("^(07\\d{8}|\\+467\\d{7})$");
// should be:
private static final Pattern PHONE = Pattern.compile("^(07\\d{8}|\\+467\\d{8})$");
```

The consequence of this is that two of our unit tests (`SeekerSpecBasedTest::validInternationalPhoneNumberLengthIsAcceptedAtRegistratioin` and `SeekerSpecBasedTest::noSpecialCharactersInInternationalPhoneNumberIsAcceptedAtRegistration`) fail.

### 3. AI use (be honest — it doesn't lower your grade)

We used AI to explain and give example of different quality characteristics and how they could fit in this project. We also used AI to explain how to structure unit tests for the decision table. We also used AI to summarize our analysis document and suggest what we should include in this reflection. However, due to lack of context, most of the suggestions were not applicable.

### 4. Judgment

Most of our decisions were made by humans. The AI was only involved as a discussion partner.

### 5. What we'd test next

It would be interesting to write more unit tests that cover more of the code to see if we can find any more bugs. As software engineers, we are more interested in writing tests and exploring the code base more than analyzing ISO quality requirements as it is often challenging to discuss.

