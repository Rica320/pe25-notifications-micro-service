# MsTeams Plugin

This file describes how to work with the Microsoft Teams plugin.
The usage of this plugin requires a configuration per channel, as described below.

## Usage

To communicate with a team, first [create a webhook in a channel](https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook?tabs=dotnet).

With the provided link, create a request to `/msteams/add`. This will store your webhook in the database, returning a team ID.

Then, using that id, you can send messages using both enpoints:
 - `/msteams/message`
 - `/notifier`

The `/notifier` endpoint is a generic endpoint, and can be used to send messages to any plugin. The `/msteams/message` endpoint is a shortcut to the `/notifier` endpoint, and is specific to the MsTeams plugin.

> **Note:** Both enpoints receive a list of ids, in order to send the same message to multiple teams at once.