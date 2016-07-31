# Threat.End

A project for govhack 2016: gamifying species education, tagging, and geolocation to involve citizens with environment improvement and data gathering.

# inVision Mockup
We have created a mockup in invision with walk through.

[inVision Mockup link Public](https://invis.io/WP84LBL32)

# Video
Youtube video while the hackerspace comes back to life.

[threat.end video] (https://youtu.be/iFvbCROmQG4)

# Technical Demo
[Page Link](http://45.55.87.246/threatend/index)  
While the UI is lacking, the API is functional.

### Basic user logins

Very basic, designed to be tied into gamification later.
/api/new-user/:username/:password/:email/:region/  
/api/login/:username/:password/  
/api/useraccount/:session/  
/api/addfriend/:session/:friend/  

### Species Searching

Returns a species information by species name  
/api/species/scientific/:speciesName/  
Returns data from the Living Atlas of Australia for a species  
/api/species/atlas/:speciesName/  
Finds all species that have been sighted within a radius of a geographical location  
/api/species/nearby/:xPos/:yPos/:radiusInMetres/  
Adds a user's sighting of a species at the geographical location  
/api/addsighting/:session/:species/:xPos/:yPos/  

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.
[leiningen]: https://github.com/technomancy/leiningen

You will also need mongodb installed

## Running

To start a web server for the application, run:

    lein ring server
    
Alteratively, to compile for tomcat use:

    lein ring uberwar

## License

Copyright © 2016 FIXME
