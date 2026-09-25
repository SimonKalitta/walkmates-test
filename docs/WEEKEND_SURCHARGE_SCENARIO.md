# Activity 4.3: weekend-surcharge change scenario

This branch is a proposed feature for regression-selection analysis, not a replacement for
the frozen lab baseline. Compare `feature/weekend-surcharge` with base commit
`072e1679a8ad2a8d41d216d4aa1839c44ec85cb6`.

## Change request

A paid booking starting on Saturday or Sunday receives a 10% weekend surcharge on its base
cost. Use the scheduled calendar date at the service location, supplied by the caller.
The current date and the day on which a booking finishes do not affect this decision.

The weekend and existing overnight surcharges are each calculated on the base cost and
added together. The trust-tier platform fee applies to that subtotal. Round the final total
to two decimal places using the existing half-up rule. Free listings remain free.
This feature does not revise the existing duration, overnight, capacity or trust-tier rules.

Compatibility decision: bookings without a scheduled date retain the original pricing,
with no weekend surcharge. Existing three-argument Java calls and JSON requests that omit
the date continue to work. This is an explicit migration rule, not a default to today's date.

## Interface

`POST /api/bookings` accepts an optional ISO calendar date:

```json
{
  "seekerId": "replace-with-existing-id",
  "listingId": "replace-with-existing-id",
  "durationMinutes": 60,
  "scheduledStartDate": "2026-10-03"
}
```

The service passes this date to the Booking, and PricingCalculator uses it. This branch
adds no scheduling screen, availability calendar or time-zone conversion.

## Inspect the change

After the instructor publishes the branch:

```bash
git fetch https://github.com/sergiorico/walkmates-test.git feature/weekend-surcharge
git diff 072e1679a8ad2a8d41d216d4aa1839c44ec85cb6 FETCH_HEAD -- src/main/java
```

Fetching and inspecting the diff does not change your working files. Students with a fork
or private repository can use the same command. You do not need to merge or reset your lab
work to perform the analysis. For a local instructor checkout, use the local branch name
instead of FETCH_HEAD.

## Your analysis

Select and prioritise tests from your existing suite. Explain which expected results need
review, which should remain unchanged, and which tests can run later. Distinguish existing
tests from gaps you propose addressing. Explain the residual risk of the selected set.
An unchanged passing test may exercise only legacy undated behaviour; inspect its fixture.

Use the normal Activity 4.3 submission route. This scenario adds no report or submission.
The branch intentionally does not include a completed regression-selection answer or a
ready-made suite of weekend feature tests.
