


-keeppackagenames com.netcetera.threeds.sdk.api.info,com.netcetera.threeds.sdk.api,com.netcetera.threeds.sdk.api.configparameters,com.netcetera.threeds.sdk.api.ui.logic,com.netcetera.threeds.sdk.infrastructure,com.netcetera.threeds.sdk.api.security,com.netcetera.threeds.sdk.api.transaction.challenge,com.netcetera.threeds.sdk.api.exceptions,org.bouncycastle.jce.provider,com.netcetera.threeds.sdk.api.transaction,com.netcetera.threeds.sdk.api.ui,com.netcetera.threeds.sdk.api.transaction.challenge.events
-adaptresourcefilecontents !jni/arm64-v8a/libae63.so,!jni/armeabi-v7a/libae63.so,!jni/x86/libae63.so,!jni/x86_64/libae63.so,!lib/arm64-v8a/libae63.so,!lib/armeabi-v7a/libae63.so,!lib/x86/libae63.so,!lib/x86_64/libae63.so,dummyfile
-dontwarn proguard.annotation.**




# Keep everyting in the Netcetera Android 3DS SDK package
-keep public class com.netcetera.threeds.sdk.** {
    public protected <fields>;    public protected <methods>;
}

-keep,allowshrinking class com.netcetera.threeds.sdk.** {
    <fields>;    <methods>;
}

# Keep everyting in Guardsquare Dexguard
-keep public class com.guardsquare.dexguard.** {
    public protected <fields>;    public protected <methods>;
}

# Keep everything from bouncycastle
-keep class org.bouncycastle.** {
    <fields>;    <methods>;
}

-keep,allowshrinking class org.bouncycastle.** {
    <fields>;    <methods>;
}

# Keep the classes from slf4j
-keep class org.slf4j.** {
    <fields>;    <methods>;
}

# Keep kotlin.KotlinVersion if present
-keep class kotlin.KotlinVersion {
    <fields>;    <methods>;
}

-keep class com.netcetera.threeds.sdk.infrastructure.getWarnings {
    int getWarnings;    int initialize;    int ThreeDS2ServiceInstance;    int get;    com.netcetera.threeds.sdk.infrastructure.ThreeDS2Service ThreeDS2Service;    byte[] getSDKVersion;    byte[] createTransaction;    byte[] cleanup;    int[] ThreeDS2ServiceInitializationCallback;    int getSDKInfo;    int addParam;    int onError;    int ConfigParameters;    <init>(java.io.InputStream,int[],byte[],int,boolean,int);
    <init>(java.io.InputStream,int[],byte[],int,boolean,int,int,int);
    int read();
    int read(byte[],int,int);
    long skip(long);
    int available();
    boolean markSupported();
    void ThreeDS2ServiceInstance();
    void ThreeDS2Service();
    int getWarnings();
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.get {
    int ThreeDS2ServiceInstance;    int ThreeDS2Service;    int getWarnings;    int initialize;    long[] get;    long[] getSDKInfo;    short createTransaction;    byte[] ThreeDS2ServiceInitializationCallback;    int cleanup;    int getSDKVersion;    int getParamValue;    <init>(java.io.InputStream,int,int,short,int,int);
    <init>(java.io.InputStream,int,int,short,int,int,int,int);
    int read();
    int read(byte[],int,int);
    long skip(long);
    int available();
    boolean markSupported();
    void ThreeDS2ServiceInstance();
    int ThreeDS2Service();
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ThreeDS2ServiceInitializationCallback {
    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.createTransaction {
    int initialize;    int getWarnings;    int get;    <init>();
    void ThreeDS2ServiceInstance(int[]);
    int ThreeDS2Service(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKInfo {
    int getWarnings;    int ThreeDS2ServiceInstance;    int get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cleanup {
    int get;}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKVersion {
    int initialize;    int ThreeDS2ServiceInstance;    char ThreeDS2Service;    char getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigParameters {
    int initialize;    char get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onCompleted {
    int initialize;    int ThreeDS2Service;    <init>();
    char[] initialize(long,char[],int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.addParam {
    int ThreeDS2Service;    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onError {
    int getWarnings;    int ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getParamValue {
    int getWarnings;    char get;    char initialize;    int ThreeDS2Service;    int ThreeDS2ServiceInstance;    int ThreeDS2ServiceInitializationCallback;    int cleanup;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigurationBuilder {
    int ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.removeParam {
    int ThreeDS2Service(java.lang.Object);
    int get(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.valueOf {
    java.lang.Integer ThreeDS2Service;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.valueOf {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.api.ThreeDS2Service {
    void initialize(android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
    void initialize(android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map);
    void initialize(android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map,com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback);
    java.util.List getWarnings();
    java.lang.String getSDKVersion();
    com.netcetera.threeds.sdk.api.info.SDKInfo getSDKInfo();
    void cleanup(android.content.Context);
    com.netcetera.threeds.sdk.api.transaction.Transaction createTransaction(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.api.transaction.Transaction createTransaction(java.lang.String,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback {
    void onCompleted();
    void onError(java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.api.configparameters.ConfigParameters {
    java.lang.String getParamValue(java.lang.String,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration$Builder {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.api.exceptions.InvalidInputException

-keep class com.netcetera.threeds.sdk.api.exceptions.SDKAlreadyInitializedException

-keep class com.netcetera.threeds.sdk.api.exceptions.SDKNotInitializedException

-keep class com.netcetera.threeds.sdk.api.exceptions.SDKRuntimeException

-keep class com.netcetera.threeds.sdk.api.info.CertificateInfo

-keep class com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType {
    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType CERTIFICATE;    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType PUBLIC_KEY;}

-keep class com.netcetera.threeds.sdk.api.info.SDKInfo

-keep class com.netcetera.threeds.sdk.api.security.Severity {
    com.netcetera.threeds.sdk.api.security.Severity MEDIUM;    com.netcetera.threeds.sdk.api.security.Severity HIGH;}

-keep class com.netcetera.threeds.sdk.api.security.Warning {
    <init>(java.lang.String,java.lang.String,com.netcetera.threeds.sdk.api.security.Severity);
}

-keep class com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters {
    <init>(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.api.transaction.Transaction {
    com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters getAuthenticationRequestParameters();
    void doChallenge(android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
    void useBridgingExtension(com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
    com.netcetera.threeds.sdk.api.ui.ProgressView getProgressView(android.app.Activity);
    void close();
}

-keep class com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters {
    java.lang.String get3DSServerTransactionID();
    java.lang.String getAcsTransactionID();
    java.lang.String getAcsRefNumber();
    java.lang.String getAcsSignedContent();
}

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver {
    void completed(com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent);
    void cancelled();
    void timedout();
    void protocolError(com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent);
    void runtimeError(com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent);
}

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.ErrorMessage {
    java.lang.String getErrorCode();
    java.lang.String getErrorDescription();
    java.lang.String getErrorDetails();
    java.lang.String getErrorComponent();
}

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent {
    java.lang.String getTransactionStatus();
}

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent {
    com.netcetera.threeds.sdk.api.transaction.challenge.ErrorMessage getErrorMessage();
}

-keep class com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent {
    java.lang.String getErrorCode();
    java.lang.String getErrorMessage();
}

-keep class com.netcetera.threeds.sdk.api.ui.ProgressView

-keep class com.netcetera.threeds.sdk.api.ui.logic.UiCustomization

-keep class com.netcetera.threeds.sdk.api.ui.logic.UiCustomization$UiCustomizationType

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.api.utils.DsRidValues {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.CertificateInfoCertificateType {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.SchemeInfo {
    <init>(com.netcetera.threeds.sdk.infrastructure.SchemeInfo$get);
}

-keep class com.netcetera.threeds.sdk.infrastructure.SchemeInfo$get {
    com.netcetera.threeds.sdk.infrastructure.SchemeInfo$get get(android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setThreeDSRequestorAppURL {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setAcsSignedContent);
    void getWarnings(java.lang.Class);
    java.lang.Object initialize(java.lang.Class);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAcsSignedContent

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getAcsRefNumber {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.completed {
    java.security.PublicKey ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.CompletionEvent {
    <init>(com.netcetera.threeds.sdk.infrastructure.getErrorMessage,java.util.List,com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$get);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent {
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.showProgress {
    com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent get(java.lang.String,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.RuntimeErrorEvent {
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.CompletionEvent);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getErrorMessage

-keep class com.netcetera.threeds.sdk.infrastructure.ProgressView {
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.util.Locale);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings,com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$get,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.getErrorMessage ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ButtonCustomization {
    void getSDKInfo();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ButtonCustomization$ThreeDS2Service {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hideProgress {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCornerRadius {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNextFocusForwardId {
    void get(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEnabled {
    java.lang.Object[] getWarnings(android.content.Context,int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEnabled {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setElevation {
    void initialize(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOutlineAmbientShadowColor {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOutlineAmbientShadowColor {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerType {
    com.netcetera.threeds.sdk.infrastructure.setLayerPaint getSDKVersion();
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerPaint {
    com.netcetera.threeds.sdk.infrastructure.setLayerPaint$ThreeDS2ServiceInstance getWarnings();
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerPaint$ThreeDS2ServiceInstance {
    java.lang.String ThreeDS2Service();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled {
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRenderEffect {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setRenderEffect$ThreeDS2Service {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBackground {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBackgroundResource {
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPadding {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintMode {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintList {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setId {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setActivated {
    com.netcetera.threeds.sdk.infrastructure.setLayerType ThreeDS2ServiceInstance(java.lang.String);
    java.util.List ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTag {
    com.netcetera.threeds.sdk.infrastructure.setActivated get(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextDirection {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPointerIcon {
    com.netcetera.threeds.sdk.infrastructure.completed ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ThreeDS2ServiceInitializationCallback;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment createTransaction;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment onError;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment onCompleted;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment addParam;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ConfigParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment visaSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment build;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment amexConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment unionSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment cbConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment eftposConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeName;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeId;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeLogo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment newSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfigurationBuilder;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeEncryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemePublicRootKeys;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logoDark;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKRuntimeException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKAlreadyInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKNotInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment CertificateInfo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getName;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getCertPrefix;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment valueOf;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeConfigurations;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getEncryptionCertificateKid;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment Severity;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment Warning;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSeverity;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSDKReferenceNumber;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getAuthenticationRequestParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment close;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ChallengeParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getProgressView;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsSignedContent;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setThreeDSRequestorAppURL;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsTransactionID;    com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback initialize(java.lang.Object[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTransitionName {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint {
    void get(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint$ThreeDS2Service);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint$ThreeDS2Service,java.lang.Runnable);
    java.lang.Object ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint$ThreeDS2ServiceInstance);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint$ThreeDS2ServiceInstance

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint$ThreeDS2Service

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFrameContentVelocity {
    void getWarnings(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setViewTranslationCallback {
    <init>(com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver);
    void ThreeDS2Service(com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent);
    void initialize();
    void ThreeDS2ServiceInstance();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent);
    void initialize(com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent);
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus {
    com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setJustificationMode);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimation {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimation {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAnimationCacheEnabled {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnHierarchyChangeListener {
    void getWarnings(java.util.Date);
    com.netcetera.threeds.sdk.api.info.SDKInfo initialize(java.util.List);
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAlwaysDrawnWithCacheEnabled {
    java.lang.Object[] ThreeDS2ServiceInstance(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAlwaysDrawnWithCacheEnabled {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAddStatesFromChildren {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimationListener {
    java.util.Locale ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHttpAuthUsernamePassword {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHttpAuthUsernamePassword {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setMinEms,com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setCursorVisible,com.netcetera.threeds.sdk.infrastructure.setCertificate);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setScroller);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setCursorVisible);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCertificate {
    void ThreeDS2ServiceInstance();
    void initialize(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay {
    java.lang.String getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setInitialScale {
    com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.setInitialScale get();
    com.netcetera.threeds.sdk.infrastructure.setWebViewClient ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebViewClient

-keep class com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist {
    com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay get();
    boolean getWarnings();
    int ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFindListener {
    com.netcetera.threeds.sdk.infrastructure.setInitialScale getWarnings(java.lang.String,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setInitialScale ThreeDS2ServiceInstance(java.lang.String,java.lang.String,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebContentsDebuggingEnabled {
    com.netcetera.threeds.sdk.infrastructure.setFindListener initialize();
    com.netcetera.threeds.sdk.infrastructure.setFindListener get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebChromeClient {
    java.lang.Object[] ThreeDS2ServiceInstance(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWebChromeClient {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDownloadListener {
    <init>(javax.net.ssl.SSLSocketFactory,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDownloadListener {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPictureListener {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setItemChecked {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRendererPriorityPolicy {
    java.lang.Integer getWarnings;    java.lang.Integer ThreeDS2Service;    java.lang.Integer ThreeDS2ServiceInstance;    android.util.Range get;}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollAlwaysVisible {
    <init>();
    boolean get(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
    java.lang.String get(com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist);
    boolean initialize(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMultiChoiceModeListener {
    com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMultiChoiceModeListener {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTranscriptMode {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint {
    com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$get ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$get

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$get {
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings getWarnings;    com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings ThreeDS2ServiceInstance;    java.lang.String getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVelocityScale {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnItemClickListener {
    java.lang.String initialize();
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings);
    java.lang.String ThreeDS2Service();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTopEdgeEffectColor {
    <init>();
    java.lang.String initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnItemSelectedListener {
    com.netcetera.threeds.sdk.infrastructure.setOnItemSelectedListener getWarnings;}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndicatorBounds {
    <init>(boolean);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScaleType {
    <init>(java.lang.Object);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageMatrixC2551$5 {
    int[] initialize;}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageAlpha {
    com.netcetera.threeds.sdk.infrastructure.setImageAlpha ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setImageAlpha {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setColorFilter {
    java.lang.String ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDividerPadding {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOrientation {
    com.netcetera.threeds.sdk.infrastructure.setOrientation ThreeDS2ServiceInstance(android.content.Context);
    java.lang.String ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOrientation {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWeightSum {
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setDividerHeight);
    com.netcetera.threeds.sdk.infrastructure.setDividerHeight ThreeDS2ServiceInstance();
    void getWarnings();
    java.lang.Long ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAligned {
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setDivider);
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setDividerHeight);
    com.netcetera.threeds.sdk.infrastructure.setDividerHeight initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignedChildIndex {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignedChildIndex {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus {
    java.lang.String initialize();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalGravity {
    void ThreeDS2ServiceInstance(java.lang.String);
    boolean get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSelectionAfterHeaderView {
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled

-keep class com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$ThreeDS2ServiceInstance {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$ThreeDS2ServiceInstance get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$ThreeDS2ServiceInstance initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDivider

-keep class com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider$getWarnings createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setDivider ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDividerHeight {
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawable initialize();
    com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled get();
    java.lang.String getWarnings();
    java.lang.Boolean ThreeDS2Service();
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawable

-keep class com.netcetera.threeds.sdk.infrastructure.setMinHeight {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setOnItemClickListener,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinWidth {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode {
    java.text.DateFormat get;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get {
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get ThreeDS2ServiceInstance;    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get getWarnings;    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get ThreeDS2Service;    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get get;    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$get {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintList {
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintMode {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintMode {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings getSDKInfo(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings ConfigParameters(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings addParam(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings getParamValue(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings onError(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintMode {
    void ThreeDS2ServiceInstance();
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList);
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintList {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintList {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintBlendMode {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMax {
    void ThreeDS2ServiceInstance(java.util.List);
    java.util.List getWarnings();
    void ThreeDS2Service(java.util.List);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMin {
    java.lang.String get(java.util.List);
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled);
    java.util.List ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress$get {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setProgress$get getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled {
    java.util.List ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled$ThreeDS2Service {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled$ThreeDS2Service ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled$ThreeDS2Service getWarnings(com.netcetera.threeds.sdk.infrastructure.setProgress);
    com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled$ThreeDS2Service initialize(java.util.List);
    com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled getWarnings();
    void get(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSmoothScrollingEnabled$ThreeDS2Service {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFillViewport {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setOnItemClickListener,java.lang.String);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setOnItemClickListener,java.lang.String,java.lang.String,java.lang.String,java.lang.String);
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithConfiguration {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithConfiguration {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithPresetSizes {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setKeyListener

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setKeyListener {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintList {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelative {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map,com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelativeWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablePadding {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLastBaselineToBottomHeight {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintBlendMode {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextSelectHandle {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintMode {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String,java.util.Map);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextLocale {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextScaleX {
    com.netcetera.threeds.sdk.api.ThreeDS2Service getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextScaleX {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLetterSpacing

-keep class com.netcetera.threeds.sdk.infrastructure.setShiftDrawingOffsetForStartOverhang {
    java.lang.String initialize(java.security.KeyPair);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setBreakStrategy {
    void get();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLineBreakStyle {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency {
    void getWarnings();
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle

-keep class com.netcetera.threeds.sdk.infrastructure.setFontFeatureSettings {
    java.lang.String initialize();
    java.lang.String getWarnings();
    java.security.PublicKey ThreeDS2ServiceInstance();
    java.security.KeyPair ThreeDS2Service();
    java.lang.String createTransaction();
    void getSDKVersion();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setJustificationMode {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void initialize();
    void ThreeDS2ServiceInstance();
    void get(com.netcetera.threeds.sdk.infrastructure.setCursorVisible);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHighlightColor {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLinksClickable {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoLinkMask {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLinkTextColor {
    com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinLines {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxLines {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeight {
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLines {
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setMinEms);
    java.lang.String get(com.netcetera.threeds.sdk.infrastructure.setCursorVisible);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinEms {
    com.netcetera.threeds.sdk.infrastructure.getCause ThreeDS2Service();
    java.lang.String getWarnings();
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String cleanup();
    com.netcetera.threeds.sdk.infrastructure.put getSDKVersion();
    com.netcetera.threeds.sdk.infrastructure.getInfo ThreeDS2ServiceInitializationCallback();
    java.lang.String getParamValue();
    com.netcetera.threeds.sdk.infrastructure.entrySet removeParam();
    java.lang.Boolean ConfigurationBuilder();
    com.netcetera.threeds.sdk.infrastructure.initCause build();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinEms {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMaxEms

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxEms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxEms {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEms {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLineSpacing {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLineSpacing {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWidth {
    java.lang.Object[] get(android.content.Context,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWidth {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWidth {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineHeight {
    java.lang.String get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHighlights {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setScroller);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setCursorVisible);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSpannableFactory {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScroller

-keep class com.netcetera.threeds.sdk.infrastructure.setAllCaps

-keep class com.netcetera.threeds.sdk.infrastructure.setCursorVisible

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart {
    void cleanup();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$getWarnings {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$getWarnings {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$get {
    void initialize();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCustomSelectionActionModeCallback {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLogo {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleTextAppearance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitle$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitleTextColor {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationOnClickListener {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationIcon {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    java.lang.Object[] initialize(android.content.Context,int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationContentDescription {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOnMenuItemClickListener {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOnMenuItemClickListener {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOverflowIcon {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOverflowIcon {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setContentInsetsAbsolute {
    <init>(java.lang.Runnable);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetEndWithActions {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBackInvokedCallbackEnabled {
    com.netcetera.threeds.sdk.infrastructure.setBackInvokedCallbackEnabled ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setFontFeatureSettings,com.netcetera.threeds.sdk.infrastructure.fx,com.netcetera.threeds.sdk.infrastructure.setLineHeight,com.netcetera.threeds.sdk.infrastructure.nh,com.netcetera.threeds.sdk.infrastructure.jh,com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings);
    void initialize(java.lang.String,com.netcetera.threeds.sdk.infrastructure.setHighlights);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.computeValue {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.findFragmentById {
    void ThreeDS2Service(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    java.lang.Object[] ThreeDS2ServiceInstance(android.content.Context,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.findFragmentByTag {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.hashCode {
    void getWarnings(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ordinal {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clone {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$4 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$4 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$3 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$3 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$2 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$2 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$5 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name$9 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$5 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$2 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$4 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$3 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$1 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$10 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$6 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$9 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$8 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$8 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$7 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$13 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$13 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$12 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$11 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$11 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$18 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$18 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$16 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$19 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$19 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$20 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$24 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$24 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass$23 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    void get();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass$ThreeDS2ServiceInstance {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getLocalizedMessage {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.initCause

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.initCause {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getCause {
    com.netcetera.threeds.sdk.infrastructure.getCause ThreeDS2ServiceInstance;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCause {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.wait {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.notifyAll {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStackTrace {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.fillInStackTrace {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.printStackTrace {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getStackTrace {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.addSuppressed {
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getInfo

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getInfo {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getVersion {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clear {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getSuppressed {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.load {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.put {
    com.netcetera.threeds.sdk.infrastructure.put initialize;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.put {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putAll {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.entrySet

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.entrySet {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.keySet {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putIfAbsent {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replaceAll {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.replace

-keep class com.netcetera.threeds.sdk.infrastructure.forEach {
    void ThreeDS2Service();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setScroller);
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.merge

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.keys$getWarnings {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProperty$initialize {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.contains {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.contains$ThreeDS2ServiceInstance {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.storeToXML {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.loadFromXML {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.x {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ac {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ae {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.af {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ai {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ai {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ak {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.al {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aj {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aj$ThreeDS2ServiceInstance {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aq {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aq$getWarnings {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.au {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.at {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ay {
    java.lang.Object[] ThreeDS2ServiceInstance$62a34fac(int,int,java.lang.Object,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ay {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aw {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ba {
    void ThreeDS2ServiceInitializationCallback();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb {
    void get();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb$5 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bi {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bi$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.bh$4 {
    java.lang.Object[] ThreeDS2Service(int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh$4 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh$4 {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bo {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bp {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bl {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bn {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bn {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.br {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bt {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bs {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.by {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cd {
    void ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cj {
    com.netcetera.threeds.sdk.infrastructure.forEach get(com.netcetera.threeds.sdk.infrastructure.merge);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cg {
    void init$0();
    void init$1();
    void init$2();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ct {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cp {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.dv {
    void ThreeDS2ServiceInstance(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dv {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ew {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev$ThreeDS2Service {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev$initialize {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev$ThreeDS2ServiceInstance {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.et {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ex {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fd {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fe {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fc {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fg {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fl {
    com.netcetera.threeds.sdk.infrastructure.setMaxEms ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.fx

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ig {
    <init>(java.util.Map);
    java.lang.String getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ig {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ii {
    boolean ThreeDS2Service();
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ii {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.il {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing);
}

-keep class com.netcetera.threeds.sdk.infrastructure.im {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ik {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing);
}

-keep class com.netcetera.threeds.sdk.infrastructure.in {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.replace);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ip {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.io {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setAllCaps);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ir {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setCursorVisible);
}

-keep class com.netcetera.threeds.sdk.infrastructure.is {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iq {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.it {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing,com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iu {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLetterSpacing);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iw {
    com.netcetera.threeds.sdk.infrastructure.setMinEms ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.setMinEms get(com.netcetera.threeds.sdk.infrastructure.replace);
    com.netcetera.threeds.sdk.infrastructure.setCursorVisible get(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iy {
    com.netcetera.threeds.sdk.infrastructure.setCertificate getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.iy {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ja {
    com.netcetera.threeds.sdk.api.transaction.Transaction get(com.netcetera.threeds.sdk.infrastructure.CompletionEvent,com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent,com.netcetera.threeds.sdk.infrastructure.setLayerType,com.netcetera.threeds.sdk.infrastructure.me,com.netcetera.threeds.sdk.infrastructure.ju,com.netcetera.threeds.sdk.infrastructure.setCacheColorHint$getWarnings,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.nh,com.netcetera.threeds.sdk.infrastructure.setProgressTintMode,com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jh

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jg {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jf {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jm {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ju

-keep class com.netcetera.threeds.sdk.infrastructure.jv {
    com.netcetera.threeds.sdk.infrastructure.ju ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization,com.netcetera.threeds.sdk.infrastructure.me);
    com.netcetera.threeds.sdk.infrastructure.ju initialize(java.util.Map,com.netcetera.threeds.sdk.infrastructure.me);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kb {
    com.netcetera.threeds.sdk.api.ui.logic.UiCustomization initialize(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
    java.util.Map ThreeDS2ServiceInstance(java.util.Map);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kb {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kb {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jy {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ka {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jx {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jx {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kd {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kf {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kc {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ki {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ki {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kk {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kk {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kj {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kn {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.me

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mi {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mf {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ml {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mn {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mt {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mq$getWarnings {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ms {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mr {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mr {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mp {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mp {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mw {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.mx {
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.mv {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.mv$initialize

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.my {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nd {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nb {
    <init>(java.lang.Object);
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nb {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ne {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ne {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nh {
    com.netcetera.threeds.sdk.infrastructure.nh get(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nh {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ng {
    void ThreeDS2Service(java.lang.Object,java.lang.String);
    java.lang.String get(java.lang.String,java.lang.String);
    void ThreeDS2ServiceInstance(int,int,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ng {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nk {
    <init>();
    void getWarnings(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nn {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nn {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nm {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nm {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ns {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nq {
    java.lang.String initialize(java.lang.String);
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nq {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.no {
    void initialize(java.lang.String);
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.Object[]);
    void ThreeDS2ServiceInstance(java.lang.String);
    void ThreeDS2Service(java.lang.String,java.lang.Object[]);
    void getWarnings(java.lang.String,com.netcetera.threeds.sdk.infrastructure.nh);
}

-keep class com.netcetera.threeds.sdk.infrastructure.nu {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nu {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nt {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nx {
    java.lang.Object[] ThreeDS2Service(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nx {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oh {
    java.lang.String[] initialize(java.lang.Object[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.og {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oj {
    java.lang.CharSequence getWarnings(java.lang.CharSequence,java.lang.CharSequence);
    boolean get(java.lang.CharSequence);
    boolean getWarnings(java.lang.CharSequence);
    boolean initialize(java.lang.CharSequence,java.lang.CharSequence);
    boolean ThreeDS2ServiceInstance(java.lang.CharSequence,java.lang.CharSequence);
    boolean initialize(java.lang.CharSequence);
    java.lang.String[] initialize(java.lang.String,java.lang.String);
    boolean initialize(java.lang.CharSequence[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oj {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oi {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.om {
    java.lang.Long ThreeDS2Service;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.om {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ol {
    java.util.Date ThreeDS2Service(java.util.Date,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ol {
    <init>();
}

-keep class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    void ThreeDS2Service();
}

-keep class org.bouncycastle.jce.provider.NcaBouncyCastleProvider$3 {
    void get(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.op {
    java.lang.String initialize(byte[]);
    byte[] getWarnings(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.op {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oq {
    <init>();
    byte[] getWarnings(java.lang.String);
    java.lang.String get(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.on {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oo$get {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.or {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.or$getWarnings initialize();
    com.netcetera.threeds.sdk.infrastructure.or$getWarnings ThreeDS2Service();
    java.security.SecureRandom getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.or$getWarnings {
    void ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String ThreeDS2Service();
    void initialize(java.lang.String);
    java.lang.String getWarnings();
    void ThreeDS2Service(java.lang.String);
    void get(java.lang.String);
    java.lang.String get();
    void getWarnings(java.lang.String);
    java.lang.String getSDKVersion();
    void ThreeDS2ServiceInitializationCallback(java.lang.String);
    java.lang.String ThreeDS2ServiceInitializationCallback();
    void getSDKVersion(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ou {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ou$4 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ou$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ov {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ow {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pb {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ox {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pc {
    java.lang.String ThreeDS2ServiceInstance();
    boolean initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pg {
    boolean ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pg {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pe {
    com.netcetera.threeds.sdk.infrastructure.pe ThreeDS2ServiceInstance;    <init>(com.netcetera.threeds.sdk.infrastructure.pe$ThreeDS2ServiceInstance,java.lang.String[]);
    void getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.pe$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.pe$ThreeDS2ServiceInstance ThreeDS2ServiceInstance;}

-keep class com.netcetera.threeds.sdk.infrastructure.pd {
    com.netcetera.threeds.sdk.infrastructure.pc get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.pi {
    com.netcetera.threeds.sdk.infrastructure.pi ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.pd get();
    com.netcetera.threeds.sdk.infrastructure.pd getWarnings();
    com.netcetera.threeds.sdk.infrastructure.pd ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pl {
    org.slf4j.Logger getWarnings;    java.lang.String initialize;    <init>();
    void getWarnings(java.lang.String);
    void initialize(java.lang.String);
    java.lang.String get();
    java.lang.String ThreeDS2ServiceInstance();
    void get(com.netcetera.threeds.sdk.infrastructure.rd);
    void ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qj {
    com.netcetera.threeds.sdk.infrastructure.qg getWarnings(java.security.spec.ECParameterSpec);
    com.netcetera.threeds.sdk.infrastructure.qg initialize(java.security.spec.ECParameterSpec,java.lang.String,java.security.SecureRandom);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qg {
    java.security.interfaces.ECPublicKey ThreeDS2ServiceInstance();
    java.security.interfaces.ECPrivateKey get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qh {
    java.security.Key ThreeDS2ServiceInitializationCallback();
    void initialize(java.lang.String);
    java.lang.String createTransaction();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qh$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.qh initialize(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qh$ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qo {
    java.security.PrivateKey onCompleted();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qo$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.qo ThreeDS2ServiceInstance(java.security.Key);
    com.netcetera.threeds.sdk.infrastructure.qo getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qo ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qo$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$initialize {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qs {
    <init>();
    boolean getWarnings();
    java.lang.String createTransaction();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qr {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qq$createTransaction {
    void ThreeDS2ServiceInstance(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq$createTransaction {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qt {
    java.lang.String ThreeDS2ServiceInstance(java.lang.String[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qt {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qz {
    void ThreeDS2Service(java.lang.String,java.lang.String);
    void ThreeDS2Service(java.lang.String,java.lang.Object);
    void ThreeDS2ServiceInstance(java.lang.String,com.netcetera.threeds.sdk.infrastructure.qh);
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
    java.lang.Long get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qo ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qx {
    byte[] get;    <init>();
    void ThreeDS2ServiceInstance(java.lang.String[]);
    void get(java.lang.String);
    java.lang.String onError();
    void initialize(java.lang.String,java.lang.String);
    void cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qz ConfigParameters();
    void ThreeDS2Service(java.lang.String,java.lang.String);
    java.lang.String ThreeDS2ServiceInitializationCallback(java.lang.String);
    void createTransaction(java.lang.String);
    java.lang.String getParamValue();
    void getSDKInfo(java.lang.String);
    java.security.Key addParam();
    void ThreeDS2ServiceInstance(java.security.Key);
    byte[] restrictedParameters();
    void getWarnings(byte[]);
    boolean removeParam();
    com.netcetera.threeds.sdk.infrastructure.pe ConfigurationBuilder();
    void get(com.netcetera.threeds.sdk.infrastructure.pe);
    void configureScheme();
    com.netcetera.threeds.sdk.infrastructure.or apiKey();
    void get(com.netcetera.threeds.sdk.infrastructure.or);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qv {
    void get(java.security.Key);
    void initialize(java.security.Key);
    void ThreeDS2Service(byte[],java.lang.String);
    void ThreeDS2ServiceInstance(java.security.Key,java.lang.String,int);
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qv {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qv {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qw {
    <init>(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qy {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.re {
    <init>();
    boolean getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rc {
    java.security.spec.ECParameterSpec get;    java.lang.String getWarnings(java.security.spec.EllipticCurve);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rb {
    <init>(byte[]);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rd {
    com.netcetera.threeds.sdk.infrastructure.rd initialize;    com.netcetera.threeds.sdk.infrastructure.rd get;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rj {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rf {
    byte[] initialize;    byte[] ThreeDS2ServiceInstance(int);
    byte[] get(long);
    boolean getWarnings(byte[],byte[]);
    byte[] ThreeDS2ServiceInstance(byte[][]);
    byte[] ThreeDS2ServiceInstance(byte[],int,int);
    byte[] ThreeDS2Service(byte[]);
    byte[] getWarnings(byte[]);
    int ThreeDS2ServiceInstance(byte[]);
    int get(int);
    int getWarnings(int);
    byte[] initialize(int,java.security.SecureRandom);
    byte[] ThreeDS2Service(int);
    java.lang.String initialize(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rf {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ri {
    java.lang.String ThreeDS2Service(java.lang.Throwable);
    java.lang.String ThreeDS2Service(java.lang.Throwable,java.lang.Class);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ri {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rg {
    java.security.MessageDigest ThreeDS2ServiceInstance(java.lang.String);
    java.security.MessageDigest initialize(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rg {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rk {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rm {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rn {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ro {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rr {
    java.lang.String ThreeDS2Service(byte[],java.lang.String);
    byte[] ThreeDS2Service(java.lang.String);
    byte[] get(java.lang.String);
    byte[] getWarnings(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rr {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rt {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rq {
    javax.crypto.Mac ThreeDS2Service(java.lang.String,java.security.Key,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rq {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rp {
    byte[] ThreeDS2ServiceInstance(byte[]);
    byte[] get(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rs {
    <init>();
}

-keepclassmembers,allowobfuscation class androidx.activity.ComponentActivity {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.activity.ComponentActivity {
    <init>(int);
    void onCreate(android.os.Bundle);
    void onSaveInstanceState(android.os.Bundle);
    java.lang.Object onRetainNonConfigurationInstance();
    java.lang.Object onRetainCustomNonConfigurationInstance();
    java.lang.Object getLastCustomNonConfigurationInstance();
    void setContentView(int);
    void setContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    void addContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    android.content.Context peekAvailableContext();
    void addOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener);
    void removeOnContextAvailableListener(androidx.activity.contextaware.OnContextAvailableListener);
    boolean onPreparePanel(int,android.view.View,android.view.Menu);
    boolean onCreatePanelMenu(int,android.view.Menu);
    boolean onMenuItemSelected(int,android.view.MenuItem);
    void onPanelClosed(int,android.view.Menu);
    void addMenuProvider(androidx.core.view.MenuProvider);
    void addMenuProvider(androidx.core.view.MenuProvider,androidx.lifecycle.LifecycleOwner);
    void addMenuProvider(androidx.core.view.MenuProvider,androidx.lifecycle.LifecycleOwner,androidx.lifecycle.Lifecycle$State);
    void removeMenuProvider(androidx.core.view.MenuProvider);
    void invalidateMenu();
    androidx.lifecycle.Lifecycle getLifecycle();
    androidx.lifecycle.ViewModelStore getViewModelStore();
    androidx.lifecycle.ViewModelProvider$Factory getDefaultViewModelProviderFactory();
    androidx.lifecycle.viewmodel.CreationExtras getDefaultViewModelCreationExtras();
    void onBackPressed();
    androidx.activity.OnBackPressedDispatcher getOnBackPressedDispatcher();
    androidx.savedstate.SavedStateRegistry getSavedStateRegistry();
    void startActivityForResult(android.content.Intent,int);
    void startActivityForResult(android.content.Intent,int,android.os.Bundle);
    void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int);
    void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle);
    void onActivityResult(int,int,android.content.Intent);
    void onRequestPermissionsResult(int,java.lang.String[],int[]);
    androidx.activity.result.ActivityResultLauncher registerForActivityResult(androidx.activity.result.contract.ActivityResultContract,androidx.activity.result.ActivityResultRegistry,androidx.activity.result.ActivityResultCallback);
    androidx.activity.result.ActivityResultLauncher registerForActivityResult(androidx.activity.result.contract.ActivityResultContract,androidx.activity.result.ActivityResultCallback);
    androidx.activity.result.ActivityResultRegistry getActivityResultRegistry();
    void onConfigurationChanged(android.content.res.Configuration);
    void addOnConfigurationChangedListener(androidx.core.util.Consumer);
    void removeOnConfigurationChangedListener(androidx.core.util.Consumer);
    void onTrimMemory(int);
    void addOnTrimMemoryListener(androidx.core.util.Consumer);
    void removeOnTrimMemoryListener(androidx.core.util.Consumer);
    void onNewIntent(android.content.Intent);
    void addOnNewIntentListener(androidx.core.util.Consumer);
    void removeOnNewIntentListener(androidx.core.util.Consumer);
    void onMultiWindowModeChanged(boolean);
    void onMultiWindowModeChanged(boolean,android.content.res.Configuration);
    void addOnMultiWindowModeChangedListener(androidx.core.util.Consumer);
    void removeOnMultiWindowModeChangedListener(androidx.core.util.Consumer);
    void onPictureInPictureModeChanged(boolean);
    void onPictureInPictureModeChanged(boolean,android.content.res.Configuration);
    void addOnPictureInPictureModeChangedListener(androidx.core.util.Consumer);
    void removeOnPictureInPictureModeChangedListener(androidx.core.util.Consumer);
    void reportFullyDrawn();
}

-keep class androidx.activity.contextaware.OnContextAvailableListener

-keep class androidx.activity.OnBackPressedDispatcher

-keepclassmembers,allowoptimization,allowobfuscation class androidx.activity.OnBackPressedDispatcher {
    <init>();
}

-keep class androidx.activity.contextaware.OnContextAvailableListener

-keep class androidx.activity.result.ActivityResultCallback

-keep class androidx.activity.result.ActivityResultLauncher

-keepclassmembers,allowoptimization,allowobfuscation class androidx.activity.result.ActivityResultLauncher {
    <init>();
}

-keep class androidx.activity.result.ActivityResultRegistry

-keepclassmembers,allowoptimization,allowobfuscation class androidx.activity.result.ActivityResultRegistry {
    <init>();
}

-keep class androidx.activity.result.contract.ActivityResultContract

-keepclassmembers,allowoptimization,allowobfuscation class androidx.activity.result.contract.ActivityResultContract {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class androidx.appcompat.app.ActionBar {
    <init>();
}

-keepclassmembers,allowobfuscation class androidx.appcompat.app.AppCompatActivity {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.appcompat.app.AppCompatActivity {
    <init>(int);
    void attachBaseContext(android.content.Context);
    void setTheme(int);
    void onPostCreate(android.os.Bundle);
    androidx.appcompat.app.ActionBar getSupportActionBar();
    void setSupportActionBar(androidx.appcompat.widget.Toolbar);
    android.view.MenuInflater getMenuInflater();
    void setContentView(int);
    void setContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    void addContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    void onConfigurationChanged(android.content.res.Configuration);
    void onPostResume();
    void onStart();
    void onStop();
    android.view.View findViewById(int);
    boolean onMenuItemSelected(int,android.view.MenuItem);
    void onDestroy();
    void onTitleChanged(java.lang.CharSequence,int);
    boolean supportRequestWindowFeature(int);
    void supportInvalidateOptionsMenu();
    void invalidateOptionsMenu();
    void onSupportActionModeStarted(androidx.appcompat.view.ActionMode);
    void onSupportActionModeFinished(androidx.appcompat.view.ActionMode);
    androidx.appcompat.view.ActionMode onWindowStartingSupportActionMode(androidx.appcompat.view.ActionMode$Callback);
    androidx.appcompat.view.ActionMode startSupportActionMode(androidx.appcompat.view.ActionMode$Callback);
    void setSupportProgressBarVisibility(boolean);
    void setSupportProgressBarIndeterminateVisibility(boolean);
    void setSupportProgressBarIndeterminate(boolean);
    void setSupportProgress(int);
    void onCreateSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder);
    void onPrepareSupportNavigateUpTaskStack(androidx.core.app.TaskStackBuilder);
    boolean onSupportNavigateUp();
    android.content.Intent getSupportParentActivityIntent();
    boolean supportShouldUpRecreateTask(android.content.Intent);
    void supportNavigateUpTo(android.content.Intent);
    void onContentChanged();
    void onSupportContentChanged();
    androidx.appcompat.app.ActionBarDrawerToggle$Delegate getDrawerToggleDelegate();
    boolean onMenuOpened(int,android.view.Menu);
    void onPanelClosed(int,android.view.Menu);
    androidx.appcompat.app.AppCompatDelegate getDelegate();
    boolean dispatchKeyEvent(android.view.KeyEvent);
    android.content.res.Resources getResources();
    boolean onKeyDown(int,android.view.KeyEvent);
    void openOptionsMenu();
    void closeOptionsMenu();
    void onNightModeChanged(int);
    void onLocalesChanged(androidx.core.os.LocaleListCompat);
}

-keep class androidx.appcompat.app.ActionBar

-keepclassmembers,allowshrinking,allowobfuscation class androidx.appcompat.app.AppCompatCallback {
    void onSupportActionModeStarted(androidx.appcompat.view.ActionMode);
    void onSupportActionModeFinished(androidx.appcompat.view.ActionMode);
    androidx.appcompat.view.ActionMode onWindowStartingSupportActionMode(androidx.appcompat.view.ActionMode$Callback);
}

-keep class androidx.appcompat.view.ActionMode

-keep class androidx.appcompat.app.AppCompatDelegate

-keepclassmembers,allowshrinking,allowobfuscation class androidx.appcompat.app.AppCompatDelegate {
    int MODE_NIGHT_FOLLOW_SYSTEM;    int MODE_NIGHT_AUTO_TIME;    int MODE_NIGHT_AUTO;    int MODE_NIGHT_NO;    int MODE_NIGHT_YES;    int MODE_NIGHT_AUTO_BATTERY;    int MODE_NIGHT_UNSPECIFIED;    int FEATURE_SUPPORT_ACTION_BAR;    int FEATURE_SUPPORT_ACTION_BAR_OVERLAY;    int FEATURE_ACTION_MODE_OVERLAY;    androidx.appcompat.app.AppCompatDelegate create(android.app.Activity,androidx.appcompat.app.AppCompatCallback);
    androidx.appcompat.app.AppCompatDelegate create(android.app.Dialog,androidx.appcompat.app.AppCompatCallback);
    androidx.appcompat.app.AppCompatDelegate create(android.content.Context,android.view.Window,androidx.appcompat.app.AppCompatCallback);
    androidx.appcompat.app.AppCompatDelegate create(android.content.Context,android.app.Activity,androidx.appcompat.app.AppCompatCallback);
    androidx.appcompat.app.ActionBar getSupportActionBar();
    void setSupportActionBar(androidx.appcompat.widget.Toolbar);
    android.view.MenuInflater getMenuInflater();
    void onCreate(android.os.Bundle);
    void onPostCreate(android.os.Bundle);
    void onConfigurationChanged(android.content.res.Configuration);
    void onStart();
    void onStop();
    void onPostResume();
    void setTheme(int);
    android.view.View findViewById(int);
    void setContentView(android.view.View);
    void setContentView(int);
    void setContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    void addContentView(android.view.View,android.view.ViewGroup$LayoutParams);
    void attachBaseContext(android.content.Context);
    android.content.Context attachBaseContext2(android.content.Context);
    void setTitle(java.lang.CharSequence);
    void invalidateOptionsMenu();
    void onDestroy();
    androidx.appcompat.app.ActionBarDrawerToggle$Delegate getDrawerToggleDelegate();
    boolean requestWindowFeature(int);
    boolean hasWindowFeature(int);
    androidx.appcompat.view.ActionMode startSupportActionMode(androidx.appcompat.view.ActionMode$Callback);
    void installViewFactory();
    android.view.View createView(android.view.View,java.lang.String,android.content.Context,android.util.AttributeSet);
    void setHandleNativeActionModesEnabled(boolean);
    boolean isHandleNativeActionModesEnabled();
    void onSaveInstanceState(android.os.Bundle);
    boolean applyDayNight();
    void setOnBackInvokedDispatcher(android.window.OnBackInvokedDispatcher);
    android.content.Context getContextForDelegate();
    void setLocalNightMode(int);
    int getLocalNightMode();
    void setDefaultNightMode(int);
    void setApplicationLocales(androidx.core.os.LocaleListCompat);
    androidx.core.os.LocaleListCompat getApplicationLocales();
    int getDefaultNightMode();
    void setCompatVectorFromResourcesEnabled(boolean);
    boolean isCompatVectorFromResourcesEnabled();
}

-keep class androidx.appcompat.view.ActionMode

-keepclassmembers,allowoptimization,allowobfuscation class androidx.appcompat.view.ActionMode {
    <init>();
}

-keep class androidx.appcompat.view.ActionMode$Callback

-keep class androidx.appcompat.widget.Toolbar

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.app.ActivityCompat$OnRequestPermissionsResultCallback {
    void onRequestPermissionsResult(int,java.lang.String[],int[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.app.ActivityCompat$RequestPermissionsRequestCodeValidator {
    void validateRequestPermissionsRequestCode(int);
}

-keepclassmembers,allowobfuscation class androidx.core.app.ComponentActivity {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.app.ComponentActivity {
    void putExtraData(androidx.core.app.ComponentActivity$ExtraData);
    void onCreate(android.os.Bundle);
    void onSaveInstanceState(android.os.Bundle);
    androidx.core.app.ComponentActivity$ExtraData getExtraData(java.lang.Class);
    androidx.lifecycle.Lifecycle getLifecycle();
    boolean superDispatchKeyEvent(android.view.KeyEvent);
    boolean dispatchKeyShortcutEvent(android.view.KeyEvent);
    boolean dispatchKeyEvent(android.view.KeyEvent);
    boolean shouldDumpInternalState(java.lang.String[]);
}

-keep class androidx.core.app.ComponentActivity$ExtraData

-keep class androidx.core.app.ComponentActivity$ExtraData

-keepclassmembers,allowoptimization,allowobfuscation class androidx.core.app.ComponentActivity$ExtraData {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class androidx.core.app.SharedElementCallback {
    <init>();
}

-keep class androidx.core.app.TaskStackBuilder

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.content.res.ResourcesCompat {
    int ID_NULL;    void clearCachesForTheme(android.content.res.Resources$Theme);
    android.graphics.drawable.Drawable getDrawable(android.content.res.Resources,int,android.content.res.Resources$Theme);
    android.graphics.drawable.Drawable getDrawableForDensity(android.content.res.Resources,int,int,android.content.res.Resources$Theme);
    int getColor(android.content.res.Resources,int,android.content.res.Resources$Theme);
    android.content.res.ColorStateList getColorStateList(android.content.res.Resources,int,android.content.res.Resources$Theme);
    float getFloat(android.content.res.Resources,int);
    android.graphics.Typeface getFont(android.content.Context,int);
    android.graphics.Typeface getCachedFont(android.content.Context,int);
    void getFont(android.content.Context,int,androidx.core.content.res.ResourcesCompat$FontCallback,android.os.Handler);
    android.graphics.Typeface getFont(android.content.Context,int,android.util.TypedValue,int,androidx.core.content.res.ResourcesCompat$FontCallback);
}

-keep class androidx.core.content.res.ResourcesCompat$FontCallback

-keep class androidx.core.content.res.ResourcesCompat$FontCallback

-keepclassmembers,allowobfuscation class androidx.core.content.res.ResourcesCompat$FontCallback {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.content.res.ResourcesCompat$FontCallback {
    void onFontRetrieved(android.graphics.Typeface);
    void onFontRetrievalFailed(int);
    void callbackSuccessAsync(android.graphics.Typeface,android.os.Handler);
    void callbackFailAsync(int,android.os.Handler);
    android.os.Handler getHandler(android.os.Handler);
}

-keep class androidx.core.os.LocaleListCompat

-keepclassmembers,allowshrinking,allowobfuscation class androidx.core.os.LocaleListCompat {
    androidx.core.os.LocaleListCompat wrap(java.lang.Object);
    androidx.core.os.LocaleListCompat wrap(android.os.LocaleList);
    java.lang.Object unwrap();
    androidx.core.os.LocaleListCompat create(java.util.Locale[]);
    java.util.Locale get(int);
    boolean isEmpty();
    int size();
    int indexOf(java.util.Locale);
    java.lang.String toLanguageTags();
    java.util.Locale getFirstMatch(java.lang.String[]);
    androidx.core.os.LocaleListCompat getEmptyLocaleList();
    androidx.core.os.LocaleListCompat forLanguageTags(java.lang.String);
    androidx.core.os.LocaleListCompat getAdjustedDefault();
    androidx.core.os.LocaleListCompat getDefault();
    boolean matchesLanguageAndScript(java.util.Locale,java.util.Locale);
    boolean equals(java.lang.Object);
    int hashCode();
    java.lang.String toString();
}

-keep class androidx.core.util.Consumer

-keep class androidx.core.view.MenuProvider

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.DialogFragment {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.Fragment {
    <init>();
}

-keepclassmembers,allowobfuscation class androidx.fragment.app.FragmentActivity {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class androidx.fragment.app.FragmentActivity {
    <init>(int);
    void onActivityResult(int,int,android.content.Intent);
    void supportFinishAfterTransition();
    void setEnterSharedElementCallback(androidx.core.app.SharedElementCallback);
    void setExitSharedElementCallback(androidx.core.app.SharedElementCallback);
    void supportPostponeEnterTransition();
    void supportStartPostponedEnterTransition();
    void onMultiWindowModeChanged(boolean);
    void onPictureInPictureModeChanged(boolean);
    void onConfigurationChanged(android.content.res.Configuration);
    void onCreate(android.os.Bundle);
    boolean onCreatePanelMenu(int,android.view.Menu);
    android.view.View onCreateView(android.view.View,java.lang.String,android.content.Context,android.util.AttributeSet);
    android.view.View onCreateView(java.lang.String,android.content.Context,android.util.AttributeSet);
    void onDestroy();
    void onLowMemory();
    boolean onMenuItemSelected(int,android.view.MenuItem);
    void onPanelClosed(int,android.view.Menu);
    void onPause();
    void onNewIntent(android.content.Intent);
    void onStateNotSaved();
    void onResume();
    void onPostResume();
    void onResumeFragments();
    boolean onPreparePanel(int,android.view.View,android.view.Menu);
    boolean onPrepareOptionsPanel(android.view.View,android.view.Menu);
    void onStart();
    void onStop();
    void supportInvalidateOptionsMenu();
    void dump(java.lang.String,java.io.FileDescriptor,java.io.PrintWriter,java.lang.String[]);
    void onAttachFragment(androidx.fragment.app.Fragment);
    androidx.fragment.app.FragmentManager getSupportFragmentManager();
    androidx.loader.app.LoaderManager getSupportLoaderManager();
    void validateRequestPermissionsRequestCode(int);
    void onRequestPermissionsResult(int,java.lang.String[],int[]);
    void startActivityFromFragment(androidx.fragment.app.Fragment,android.content.Intent,int);
    void startActivityFromFragment(androidx.fragment.app.Fragment,android.content.Intent,int,android.os.Bundle);
    void startIntentSenderFromFragment(androidx.fragment.app.Fragment,android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle);
}

-keep class androidx.core.app.SharedElementCallback

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.FragmentFactory {
    <init>();
}

-keep class androidx.fragment.app.FragmentManager

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.FragmentManager {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.FragmentManager$FragmentLifecycleCallbacks {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class androidx.fragment.app.FragmentTransaction {
    <init>();
}

-keep class androidx.lifecycle.Lifecycle

-keepclassmembers,allowoptimization,allowobfuscation class androidx.lifecycle.Lifecycle {
    <init>();
}

-keep class androidx.lifecycle.Lifecycle$State

-keep class androidx.lifecycle.LifecycleOwner

-keepclassmembers,allowoptimization,allowobfuscation class androidx.lifecycle.LiveData {
    <init>();
}

-keep class androidx.lifecycle.ViewModelProvider$Factory

-keep class androidx.lifecycle.ViewModelStore

-keepclassmembers,allowoptimization,allowobfuscation class androidx.lifecycle.ViewModelStore {
    <init>();
}

-keep class androidx.lifecycle.viewmodel.CreationExtras

-keepclassmembers,allowoptimization,allowobfuscation class androidx.lifecycle.viewmodel.CreationExtras {
    <init>();
}

-keep class androidx.loader.app.LoaderManager

-keepclassmembers,allowoptimization,allowobfuscation class androidx.loader.app.LoaderManager {
    <init>();
}

-keep class androidx.savedstate.SavedStateRegistry

-keepclassmembers,allowoptimization,allowobfuscation class androidx.savedstate.SavedStateRegistry {
    <init>();
}

-keep class com.google.android.gms.common.GoogleApiAvailability {
    int isGooglePlayServicesAvailable(android.content.Context);
    com.google.android.gms.common.GoogleApiAvailability getInstance();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.google.android.gms.common.GoogleApiAvailability {
    <init>();
}

-keep class com.google.android.gms.common.GooglePlayServicesNotAvailableException

-keep class com.google.android.gms.common.GooglePlayServicesRepairableException

-keepclassmembers,allowobfuscation class com.google.android.gms.common.api.internal.zabw {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.google.android.gms.common.api.internal.zabw {
    void zaa();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.google.android.gms.common.api.internal.zabx {
    <init>(com.google.android.gms.common.api.internal.zabw);
    void onReceive(android.content.Context,android.content.Intent);
    void zaa(android.content.Context);
    void zab();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable {
    <init>();
}

-keep class com.google.android.gms.security.ProviderInstaller {
    void installIfNeeded(android.content.Context);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.google.android.gms.security.ProviderInstaller {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.google.android.gms.tasks.Task {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.asn1.ASN1Object {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.asn1.ASN1Sequence {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.asn1.ASN1Set {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.jcajce.provider.util.AlgorithmProvider {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.jce.provider.BouncyCastleProvider {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.bike.BIKEKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.cmce.CMCEKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.falcon.FalconKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.lms.LMSKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.newhope.NHKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.ntru.NTRUKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.picnic.PicnicKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.sphincsplus.SPHINCSPlusKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class org.bouncycastle.util.Strings {
    <init>();
}

-keep class org.slf4j.Logger {
    boolean isDebugEnabled();
}

-keep class org.slf4j.LoggerFactory {
    org.slf4j.Logger getLogger(java.lang.Class);
}

-keepclassmembers,allowoptimization,allowobfuscation class org.slf4j.helpers.MessageFormatter {
    <init>();
}

-keepclassmembers !abstract class !com.google.ads.** extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
    public void set*(...);
}

-keepclassmembers !abstract class * {
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

# Enumerations.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * extends javax.net.ssl.SSLSocketFactory {
    ** sslParameters;    ** context;    ** delegate;}

# #################################
# Keep #
# # Keep API enumerations ##
-keepclassmembers enum  com.netcetera.threeds.sdk.api.** {
    <fields>;    <methods>;
}
