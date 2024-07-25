package com.project.panel.model

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
    var farm: String = "Farming in games",
    var sell: String = "Auto Sell",
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
    var userNotActive: String = "User not used",
    var maFile: MaFile = MaFile(),
    var passwordFile: PasswordFile = PasswordFile(),
    var action: AccountAction = AccountAction(),
    var hero: HeroAction = HeroAction(),
    var dropAccount: DropAccount = DropAccount(),
)

data class Farm(
    var name: String = "Farm in games",
    var service: FarmService = FarmService(),
)

data class FarmService(
    var auth: String = "[%s] | Authorization",
    var dota: String = "[%s] | Dota 2"
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
    var langApp: String = "Application language",
    var pathSteam: String = "Location to Steam",
    var steamNotExist: String = "Specify the path to steam.exe",
    var specify: String = "Specify",
    var specifySteamExe: String = "Specify the path to steam.exe"
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
    var pathSteam: String = "This file is not Steam, please specify the file steam.exe"
)

data class Warning(
    var name: String = "Warning!",
    var notAllAccount: String = "Not all accounts had a password found in the file you provided.",
)

data class Success (
    var name: String = "Success",
    var maFile: String = "Perhaps these accounts already exist, or the files are not valid!",
    var import: String = "Accounts successfully imported! Wait while background authorization and account validation occurs",
    var auth: String = "Account '%s' has been successfully authorized! The user is available to work.",
    var pathSteam: String = "You have successfully changed the path to Steam"
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

data class HeroAction(
    var title: String = "Priority Heroes",
    var search: String = "Search for Dota2 heroes",
    var count: String = "Heroes available: ",
    var random: String = "Random Hero",
    var notFound: String = "Nothing found..",
    var hint: String = "To change a specific hero, click on it and make a choice. If you want to reset your specific choice, click on the cross, a random hero will be selected"
)

data class DropAccount(
    var title: String = "Drop account",
    var description: String = "Are you sure you want to delete the selected accounts?",
    var delete: String = "Delete",
    var cancel: String = "Cancel"
)