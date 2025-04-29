package com.processout.sdk.api.model.response

/**
 * Supported card schemes and co-schemes.
 */
enum class POCardScheme(
    val rawValue: String,
    val displayName: String
) {

    /** American Express is a key credit card around the world. */
    AMEX(
        rawValue = "american express",
        displayName = "American Express"
    ),

    /** Atos Private Label is a private label credit card that is branded for Atos. */
    ATOS_PRIVATE_LABEL(
        rawValue = "atos private label",
        displayName = "Atos Private Label"
    ),

    /** Aura is a domestic debit and credit card brand of Brazil. */
    AURA(
        rawValue = "aura",
        displayName = "Aura"
    ),

    /** Bancontact is the most popular online payment method in Belgium. */
    BANCONTACT(
        rawValue = "bancontact",
        displayName = "Bancontact"
    ),

    /** BC Global is a South Korean domestic card brand with international acceptance. */
    BC_GLOBAL(
        rawValue = "global bc",
        displayName = "BC Global"
    ),

    /** Cabal is a local credit and debit card payment method based in Argentina. */
    CABAL(
        rawValue = "cabal",
        displayName = "Cabal"
    ),

    /** CARNET is a leading brand of Mexican acceptance, with more than 50 years of experience. */
    CARNET(
        rawValue = "carnet",
        displayName = "CARNET"
    ),

    /** Cartes Bancaires is France's local card scheme and the most widely used payment method in the region. */
    CARTE_BANCAIRE(
        rawValue = "carte bancaire",
        displayName = "Cartes Bancaires"
    ),

    /** Cirrus is a worldwide interbank network that provides cash to Mastercard cardholders. */
    CIRRUS(
        rawValue = "cirrus",
        displayName = "Cirrus"
    ),

    /** Cielo is a domestic debit and credit card brand of Brazil. */
    CIELO(
        rawValue = "cielo",
        displayName = "Cielo"
    ),

    /** Comprocard is a domestic debit and credit card brand of Brazil. */
    COMPROCARD(
        rawValue = "comprocard",
        displayName = "Comprocard"
    ),

    /** Dankort is a national debit card of Denmark. */
    DANKORT(
        rawValue = "dankort",
        displayName = "Dankort"
    ),

    /** DinaCard is a national payment card of the Republic of Serbia. */
    DINA_CARD(
        rawValue = "dinacard",
        displayName = "DinaCard"
    ),

    /** Diners charge card. */
    DINERS_CLUB(
        rawValue = "diners club",
        displayName = "Diners Club"
    ),

    /** Diners charge card. */
    DINERS_CLUB_CARTE_BLANCHE(
        rawValue = "diners club carte blanche",
        displayName = "Diners Club Carte Blanche"
    ),

    /** Diners charge card. */
    DINERS_CLUB_INTERNATIONAL(
        rawValue = "diners club international",
        displayName = "Diners Club International"
    ),

    /** Diners charge card. */
    DINERS_CLUB_UNITED_STATES_AND_CANADA(
        rawValue = "diners club united states & canada",
        displayName = "Diners Club United States & Canada"
    ),

    /** Discover is a credit card brand issued primarily in the United States. */
    DISCOVER(
        rawValue = "discover",
        displayName = "Discover"
    ),

    /** Elo is a domestic debit and credit card brand of Brazil. */
    ELO(
        rawValue = "elo",
        displayName = "Elo"
    ),

    /** GE Capital is the financial services division of General Electric. */
    GE_CAPITAL(
        rawValue = "ge capital",
        displayName = "GE Capital"
    ),

    /** A Girocard payment method. */
    GIROCARD(
        rawValue = "girocard",
        displayName = "girocard"
    ),

    /** Giropay is an Internet payment system in Germany. */
    GIROPAY(
        rawValue = "giropay",
        displayName = "giropay"
    ),

    /** Hipercard is a domestic debit and credit card brand of Brazil. */
    HIPERCARD(
        rawValue = "hipercard",
        displayName = "Hipercard"
    ),

    /** An iD payment card. */
    ID_CREDIT(
        rawValue = "idCredit",
        displayName = "iD Credit"
    ),

    /** An Interac payment method. */
    INTERAC(
        rawValue = "interac",
        displayName = "Interac"
    ),

    /** JCB is a major card issuer and acquirer from Japan. */
    JCB(
        rawValue = "jcb",
        displayName = "JCB"
    ),

    /** Maestro is a brand of debit cards and prepaid cards owned by Mastercard. */
    MAESTRO(
        rawValue = "maestro",
        displayName = "Maestro"
    ),

    /** Mada is a national payment scheme of Saudi Arabia. */
    MADA(
        rawValue = "mada",
        displayName = "mada"
    ),

    /** Mastercard is a market leading card scheme worldwide. */
    MASTERCARD(
        rawValue = "mastercard",
        displayName = "Mastercard"
    ),

    /** A Meeza payment card. */
    MEEZA(
        rawValue = "meeza",
        displayName = "Meeza"
    ),

    /** A Mir payment card. */
    MIR(
        rawValue = "nspk mir",
        displayName = "Mir"
    ),

    /** A Nanaco payment card. */
    NANACO(
        rawValue = "nanaco",
        displayName = "nanaco"
    ),

    /** UK Credit Cards issued by NewDay. */
    NEWDAY(
        rawValue = "newday",
        displayName = "NewDay"
    ),

    /** NYCE is an interbank network connecting the ATMs of various financial institutions in the United States and Canada. */
    NYCE(
        rawValue = "nyce",
        displayName = "NYCE"
    ),

    /** Ourocard is a domestic debit and credit card brand of Brazil. */
    OUROCARD(
        rawValue = "ourocard",
        displayName = "Ourocard"
    ),

    /** A PagoBANCOMAT payment card. */
    PAGO_BANCOMAT(
        rawValue = "pagoBancomat",
        displayName = "PagoBANCOMAT"
    ),

    /** A PostFinance AG payment card. */
    POST_FINANCE(
        rawValue = "postFinance",
        displayName = "PostFinance Card"
    ),

    /** Private Label is a type of credit card that is branded for a specific retailer or brand. */
    PRIVATE_LABEL(
        rawValue = "private label",
        displayName = "Private Label"
    ),

    /** A QUICPay payment card. */
    QUIC_PAY(
        rawValue = "quicPay",
        displayName = "QUICPay"
    ),

    /** RuPay is an Indian multinational financial services and payment service system. */
    RUPAY(
        rawValue = "rupay",
        displayName = "RuPay"
    ),

    /** Sodexo is a company that offers prepaid meal cards and other prepaid services. */
    SODEXO(
        rawValue = "sodexo",
        displayName = "Sodexo"
    ),

    /** A Suica payment card. */
    SUICA(
        rawValue = "suica",
        displayName = "Suica"
    ),

    /** A T-money payment card. */
    T_MONEY(
        rawValue = "tmoney",
        displayName = "T-money"
    ),

    /** TROY (acronym of Türkiye’nin Ödeme Yöntemi) is a Turkish card scheme. */
    TROY(
        rawValue = "troy",
        displayName = "TROY"
    ),

    /** UnionPay is the world’s biggest card network with more than 7 billion cards issued. */
    UNION_PAY(
        rawValue = "china union pay",
        displayName = "UnionPay"
    ),

    /**
     * V Pay is a Single Euro Payments Area (SEPA) debit card for use in Europe, issued by Visa Europe.
     * It uses the EMV chip and PIN system and may be co-branded with various national debit card schemes such as the German Girocard.
     */
    V_PAY(
        rawValue = "vpay",
        displayName = "V Pay"
    ),

    /** Verve is Africa's most successful card brand. */
    VERVE(
        rawValue = "verve",
        displayName = "Verve"
    ),

    /** Visa is the largest global card network in the world by transaction value, ubiquitous worldwide. */
    VISA(
        rawValue = "visa",
        displayName = "Visa"
    ),

    /** A Visa Electron debit card. */
    VISA_ELECTRON(
        rawValue = "electron",
        displayName = "Visa Electron"
    ),

    /** A WAON payment card. */
    WAON(
        rawValue = "waon",
        displayName = "WAON"
    )
}
