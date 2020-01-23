# sky-bet
Response to exercise

This is a fully functioning API utilizing Spring Boot JPA, compiled on Java 11.

When started (com.skyb.Application is the main class) it will start on localhost:3050.

Note, endpoints in the /control/** path a protected by Basic Auth - see application.properties, hopefully you have something like postman to test the endpoints.

The database behind it is h2, so you shouldn't need to install anything. The database is created from the init.sql script in resources, though the structure is fairly flat at the moment to expedite things.

The demo deals solely with placing a bet and controlling the game, it doesn't try to check if someone has enough funds etc.

The setup should allow for any number of bets to be made by a customer against a game.

The com.skyb.entity.Bet enumeration should in theory be the only thing that needs adjusting to add more bets etc.

Hopefully by looking at the controllers (RouletteController and RouletteAdminController) it should be clear what endpoints are available.

I did also include the swagger UI for the endpoints on the /public/** path. This can be found at http://localhost:3050/sky-bet/swagger-ui.html 
