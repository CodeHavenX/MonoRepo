# Alpaca Scheduler Back-End

## Getting Started
1. Download Docker on your machine. We use [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- If you are unable to launch Docker Desktop when on Ubuntu 24.04, try the following:
https://forums.docker.com/t/docker-desktop-not-working-on-ubuntu-24-04/141054/2

2. Follow the steps [here](https://supabase.com/docs/guides/cli/getting-started) to install a local supabase instance.
3. Once you've installed supabase, `cd MonoRepo/alpaca-scheduler` and enter `supabase start` . This will be used for local development.
- The first time will take ~XX minute to complete the first time. 

### Ubuntu Work Around for Docker Desktop
```bash
sudo sysctl -w kernel.apparmor_restrict_unprivileged_userns=0
sudo systemctl --user restart docker-desktop
```

## Sequence Diagram
Here is a simple flow chart:

```mermaid
sequenceDiagram
    Alicia->>+Reservation: getAvailableTimes<br>(appointmentType, period, staff?)
    Reservation->>+Configuration: getConfiguration<br>(appointmentType)
    Configuration-->>-Reservation: Duration
    Reservation->>+Calendar: getAvailableTimeSlots<br>(period, duration, staff?)
    Calendar-->>-Reservation: List<AvailabilityTimeSlot>
    Reservation-->>-Alicia: List<AvailabilityTimeSlot>
    Alicia->>+Reservation: book(staff?, timeSlot, user)
    Reservation->>+Calendar: book(timeSlot, staff?, user)
    Calendar-->>-Reservation: RequestConfirmation
    Reservation->>Comms: notify(timeSlot, staff, user)
    Reservation-->>-Alicia: RequestConfirmation
    Comms-->>Alicia: notify(timeSlot, staff, user)
```

Test

```mermaid
sequenceDiagram
    participant Cesar
    participant Reservation
    participant Calendar
    participant Comms
    Comms-->>Cesar: notify(timeSlot, staff, user)
    Cesar->>+Reservation: getBookedTimes<br>(period, staff)
    Reservation->>+Calendar: getBookedTimeSlots<br>(period)
    Calendar-->>-Reservation: List<TimeSlot>
    Reservation-->>-Cesar: List<TimeSlot>
    Cesar->>+Reservation: confirm(staff, timeSlot, user)
    Reservation->>+Calendar: confirm(timeSlot, staff, user)
    Calendar-->>-Reservation: BookedConfirmation
    Reservation-->>Comms: notify(timeSlot, staff, user)
    Reservation-->>-Cesar: BookedConfirmation
```