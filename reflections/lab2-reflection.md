# Lab Reflection — WalkMates

**Lab:** 2  
**Pair:** Simon Kalitta (sika2400), Kiryl Yasny (kiya2400)  

# Update commits
**Repo commit/tag:** [https://github.com/SimonKalitta/walkmates-test/commits/main/](https://github.com/SimonKalitta/walkmates-test/commits/main/), commits `8820add` to `c0f1e5b`.

---

### 1. What we did
We raised branch coverage to 100% for `PricingCalculator` by adding tests for shelter volunteer listings always should be free, overnight bookings should include a 20% surcharge, and bookings exactly 480 minutes shouldn't include the 20% surcharge (FR-4.3). The provided test already covered a non-overnight booking. 

Before:
![](2026-09-29_14-36-30.png)

After:
![](2026-09-30_07-35-14.png)

### 2. What we found
When writing the test for bookings exactly 480 minutes, we notice that it failed and the expected result included the 20% surcharge when it shouldn't:
```
AssertionFailedError: 
    expected: 716.8
    but was: 860.16
```

The 600-minute overnight test triggered the surcharge line and together with the provided 60-minute test reached 100% branch coverage. Both tests produce the same result but only differ at exactly 480 minutes. Our boundary test at 480 minutes (from FR-4.3, "strictly greater than") failed against the provided code.

When looking in the `PricingCalculator::priceFor` `if`-statement, we can see that it checks the provided minutes to be greater or equal to the `OVERNIGHT_THRESHOLD_MINUTES`.

```java
if (booking.getDurationMinutes() >= OVERNIGHT_THRESHOLD_MINUTES)
```

This break the FR-4.3 requirement. It says that the overnight surcharge only applies when duration is greater than 480 minutes. Exactly 480 minutes does not trigger a surcharge. 

This means that the provided duration shouldn't be compared equal to the threshold, only greater than the threshold:

```java
if (booking.getDurationMinutes() > OVERNIGHT_THRESHOLD_MINUTES)
```

### 3. AI use (be honest — it doesn't lower your grade)


### 4. Judgment


### 5. What we'd test next
