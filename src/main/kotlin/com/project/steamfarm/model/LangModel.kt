package com.project.steamfarm.model

const val DEFAULT_LANGUAGE = "default"

data class LangModel(
    var code: String = DEFAULT_LANGUAGE,
    var name: String = "Default",
    var file: String? = null,
    var text: Text = Text()
)

data class Text(
    var closeSection: String = "Press 'ESC' to close this window.",
    var description: String = "Control Panel",
    var welcome: String = "Welcome!",
    var choosePointMenu: String = "Select an item from the menu",
    var readData: String = "Reading data..",
    var menu: Menu = Menu(),
    var accounts: Accounts = Accounts(),
    var farm: Farm = Farm(),
    var sell: Sell = Sell(),
    var subscribe: Subscribe = Subscribe(),
    var cloud: Cloud = Cloud(),
    var settings: Settings = Settings(),
    var success: Success = Success(),
    var warning: Warning = Warning(),
    var failure: Failure = Failure()
)

data class Menu(
    var basic: String = "Basic",
    var other: String = "Other",
    var accounts: String = "Accounts",
    var farm: String = "Farm in game",
    var sell: String = "Sell items",
    var subscribe: String = "Subscribe",
    var cloud: String = "Cloud",
    var settings: String = "Settings",
)

data class Accounts(
    var name: String = "Accounts",
    var search: String = "Search account",
    var import: String = "Import",
    var sorting: String = "Sorting",
    var selected: String = "Selected accounts: ",
    var numberOfAccounts: String = "Number of accounts: ",
    var notFound: String = "Nothing found..",
    var hintToImport: String = "To import accounts, click 'Import'",
    var unused: String = "User not used",
    var maFile: MaFile = MaFile(),
    var passwordFile: PasswordFile = PasswordFile(),
    var action: AccountAction = AccountAction(),
)

data class Farm(
    var name: String = "Farm in games",
)

data class Sell(
    var name: String = "Sell items",
)

data class Subscribe(
    var name: String = "Subscribe",
)

data class Cloud(
    var name: String = "Cloud",
)

data class Settings(
    var name: String = "Current Settings",
    var langApp: String = "Application language"
)

data class MaFile(
    var name: String = "Working with .maFile",
    var drag: String = "Drag and drop files into this field",
    var file: String = "Select files",
    var hint: String = "Upload files with the extension .maFile to this field, or select a file by clicking on the button"
)

data class PasswordFile(
    var name: String = "Setting a password",
    var hint: String = "You can upload a file with passwords, the contents of which should be in the format login:password",
)

data class Failure (
    var name: String = "An error has occurred",
    var maFile: String = "The files you selected are not .maFile, please select other files",
    var passwordsNotFound: String = "No passwords were found in this file for the accounts you are importing.",
    var auth: String = "An error occurred while authorizing account '%s'! You may have entered the wrong password.."
)

data class Warning(
    var name: String = "Warning!",
    var notAllAccount: String = "Not all accounts had a password found in the file you provided.",
)

data class Success (
    var name: String = "Success",
    var maFile: String = "Now you need to set a password for your accounts, please select an action in this window",
    var import: String = "Accounts successfully imported! Wait while background authorization and account validation occurs",
    var auth: String = "Account '%s' has been successfully authorized! The user is available to work."
)

data class AccountAction(
    var unknown: String = "Unknown",
    var yourHero: String = "Your chosen heroes",
    var createdDate: String = "Date added",
    var hours: String = "hours",
    var clockInGame: String = "Clock in Dota 2",
    var lastDropDate: String = "Last drop date",
    var chooseHero: String = "Choose hero",
    var enableFarmGame: String = "Add for farming",
    var disableFarmGame: String = "Remove from farm",
    var dropAccount: String = "Drop account"
)