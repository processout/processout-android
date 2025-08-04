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

-keep class com.netcetera.threeds.sdk.infrastructure.ThreeDS2Service {
    byte[][] getWarnings(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.get {
    byte[] ThreeDS2Service;    int[] ThreeDS2ServiceInstance;    int[] getWarnings;    int[] get;    int[] initialize;    int ThreeDS2ServiceInitializationCallback;    int[] cleanup;    byte[][] getSDKInfo;    int[] createTransaction;    byte[] getSDKVersion;    byte[] onError;    int addParam;    int getParamValue;    int onCompleted;    int ConfigParameters;    int apiKey;    int ConfigurationBuilder;    <init>(java.io.InputStream,int,byte[],byte[][]);
    <init>(java.io.InputStream,int,byte[],byte[][],int,int);
    int read();
    int read(byte[]);
    int read(byte[],int,int);
    long skip(long);
    int available();
    void close();
    boolean markSupported();
    void mark(int);
    void reset();
    byte[][] ThreeDS2ServiceInstance(byte[][]);
    int get();
    void get(byte[],int,byte[],int);
    void initialize();
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ThreeDS2ServiceInstance {
    void getWarnings(byte[],byte,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ThreeDS2ServiceInitializationCallback {
    int getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.createTransaction {
    int getWarnings;    int get;    int ThreeDS2Service;    <init>();
    void ThreeDS2Service(int[]);
    int getWarnings(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKInfo {
    int getWarnings;    int ThreeDS2Service;    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cleanup {
    int ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKVersion {
    int initialize;    int getWarnings;    char ThreeDS2ServiceInstance;    char get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigParameters {
    int initialize;    char ThreeDS2Service;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onCompleted {
    int ThreeDS2Service;    int getWarnings;    <init>();
    char[] ThreeDS2ServiceInstance(long,char[],int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.addParam {
    int get;    int ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onError {
    int ThreeDS2Service;    int getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getParamValue {
    int ThreeDS2ServiceInstance;    char get;    char ThreeDS2Service;    int initialize;    int getWarnings;    int createTransaction;    int getSDKVersion;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigurationBuilder {
    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.removeParam {
    int get(java.lang.Object);
    int ThreeDS2ServiceInstance(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.CertificateInfoCertificateType {
    java.lang.Integer initialize;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.CertificateInfoCertificateType {
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

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCertPrefix {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.values {
    <init>(com.netcetera.threeds.sdk.infrastructure.values$ThreeDS2ServiceInstance);
}

-keep class com.netcetera.threeds.sdk.infrastructure.values$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.values$ThreeDS2ServiceInstance ThreeDS2ServiceInstance(android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.close {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setAcsTransactionID);
    void ThreeDS2Service(java.lang.Class);
    java.lang.Object get(java.lang.Class);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAcsTransactionID

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAcsSignedContent {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.completed {
    java.security.PublicKey getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getErrorComponent {
    <init>(com.netcetera.threeds.sdk.infrastructure.getErrorMessage,java.util.List,com.netcetera.threeds.sdk.infrastructure.setFriction$ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getTransactionStatus {
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getMessageVersionNumber {
    com.netcetera.threeds.sdk.infrastructure.getTransactionStatus ThreeDS2Service(java.lang.String,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.showProgress {
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.getErrorComponent);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getErrorMessage

-keep class com.netcetera.threeds.sdk.infrastructure.ProgressView {
    void get(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.util.Locale);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings,com.netcetera.threeds.sdk.infrastructure.setFriction$ThreeDS2Service,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.getErrorMessage get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent {
    void ThreeDS2ServiceInitializationCallback();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent$ThreeDS2Service {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hideProgress {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCornerRadius {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDarkTextColor {
    java.lang.Object[] initialize(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDarkTextColor {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollContainer {
    java.lang.Object[] getWarnings(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollContainer {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStateListAnimator {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStateListAnimator {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutParams {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutParams {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollBarSize {
    com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled cleanup();
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled {
    com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled$ThreeDS2ServiceInstance ThreeDS2Service();
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollbarFadingEnabled$ThreeDS2ServiceInstance {
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerPaint {
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerType {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayerType$get {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRenderEffect {
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setBackgroundDrawable {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintBlendMode {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundGravity {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintList {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setForegroundTintMode {
    void initialize(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPadding {
    com.netcetera.threeds.sdk.infrastructure.setScrollBarSize ThreeDS2ServiceInstance(java.lang.String);
    java.util.List initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setActivated {
    com.netcetera.threeds.sdk.infrastructure.setPadding ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAnimation {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinimumHeight {
    void initialize(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOverScrollMode {
    com.netcetera.threeds.sdk.infrastructure.completed initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ThreeDS2ServiceInitializationCallback;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment cleanup;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getParamValue;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment onCompleted;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ConfigParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment addParam;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment build;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment mastercardSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment amexConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment dinersSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment eftposConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment cbConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeLogoDark;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeId;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment newSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeName;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeEncryptionPublicKeyId;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfigurationBuilder;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemePublicRootKeys;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logoDark;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKNotInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKAlreadyInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKRuntimeException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment toString;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment CertificateInfo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment values;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getCertPrefix;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKInfo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getEncryptionCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getEncryptionCertificateKid;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment Warning;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment AuthenticationRequestParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getDeviceData;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getMessageVersion;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ChallengeParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment TransactionBridgingMessageExtensionVersion;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getValue;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsTransactionID;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsSignedContent;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsRefNumber;    com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener get(java.lang.Object[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextDirection {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled$ThreeDS2ServiceInstance);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled$ThreeDS2ServiceInstance,java.lang.Runnable);
    java.lang.Object ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled$initialize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled$initialize

-keep class com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled$ThreeDS2ServiceInstance

-keep class com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureHint {
    int initialize;    <init>(com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent);
    void ThreeDS2Service();
    void getWarnings();
    void getWarnings(com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent);
    void initialize(com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent);
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setClipChildren {
    com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled get(com.netcetera.threeds.sdk.infrastructure.setBreakStrategy);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setClipChildren {
    void get();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTransitionGroup {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setClipToPadding {
    java.lang.Object[] getWarnings(android.content.Context,int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setClipToPadding {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimation {
    void get(java.util.Date);
    com.netcetera.threeds.sdk.api.info.SDKInfo getWarnings(java.util.List);
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOnHierarchyChangeListener {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutTransition {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutMode {
    java.util.Locale getWarnings(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimationListener {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setLines,com.netcetera.threeds.sdk.infrastructure.setAddStatesFromChildren);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setAllCaps,com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAddStatesFromChildren {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setFilters);
    void get(com.netcetera.threeds.sdk.infrastructure.setExtractedText);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay {
    void getWarnings();
    void initialize(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHttpAuthUsernamePassword {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay {
    com.netcetera.threeds.sdk.infrastructure.setFindListener initialize();
    com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable

-keep class com.netcetera.threeds.sdk.infrastructure.setFindListener {
    com.netcetera.threeds.sdk.infrastructure.setHttpAuthUsernamePassword ThreeDS2ServiceInstance();
    boolean initialize();
    int ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebViewClient {
    com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay ThreeDS2Service(java.lang.String,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay getWarnings(java.lang.String,java.lang.String,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist {
    com.netcetera.threeds.sdk.infrastructure.setWebViewClient ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.setWebViewClient get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPictureListener {
    <init>(javax.net.ssl.SSLSocketFactory,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPictureListener {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebContentsDebuggingEnabled {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDataDirectorySuffix {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setItemChecked {
    java.lang.Integer ThreeDS2Service;    java.lang.Integer getWarnings;    java.lang.Integer get;    android.util.Range initialize;}

-keep class com.netcetera.threeds.sdk.infrastructure.setAdapter {
    <init>();
    boolean getWarnings(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setFindListener);
    boolean initialize(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle {
    com.netcetera.threeds.sdk.infrastructure.setLayoutAnimationListener ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setLayoutAnimationListener);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMultiChoiceModeListener {
    java.lang.Object[] get$22d121da(int,int,java.lang.Object,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMultiChoiceModeListener {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSelector {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFriction {
    com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings get();
    com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setFriction$ThreeDS2Service ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFriction$ThreeDS2Service

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFriction$ThreeDS2Service {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings ThreeDS2ServiceInstance;    com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings ThreeDS2Service;    java.lang.String ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEdgeEffectColor {
    java.lang.String initialize();
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings);
    java.lang.String getWarnings();
    java.lang.String ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVelocityScale {
    <init>();
    java.lang.String ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTopEdgeEffectColor {
    com.netcetera.threeds.sdk.infrastructure.setTopEdgeEffectColor ThreeDS2ServiceInstance;}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnGroupCollapseListener {
    <init>(boolean);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScaleType {
    <init>(java.lang.Object);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineY10307$5 {
    int[] get;}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageMatrix {
    com.netcetera.threeds.sdk.infrastructure.setImageMatrix ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setImageMatrix {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignBottom {
    java.lang.String ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCropToPadding {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDividerPadding {
    com.netcetera.threeds.sdk.infrastructure.setDividerPadding ThreeDS2ServiceInstance(android.content.Context);
    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDividerPadding {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setShowDividers {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setDividerHeight);
    com.netcetera.threeds.sdk.infrastructure.setDividerHeight get();
    void ThreeDS2ServiceInstance();
    java.lang.Long getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOrientation {
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setVerticalGravity);
    java.lang.String ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setDividerHeight);
    com.netcetera.threeds.sdk.infrastructure.setDividerHeight getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignedChildIndex {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignedChildIndex {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWeightSum {
    java.lang.String ThreeDS2Service();
    void get(com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMeasureWithLargestChildEnabled {
    void ThreeDS2ServiceInstance(java.lang.String);
    boolean get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setGravity

-keep class com.netcetera.threeds.sdk.infrastructure.setGravity$initialize {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setGravity$initialize get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setGravity$initialize ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setGravity ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalGravity

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize get(com.netcetera.threeds.sdk.infrastructure.setGravity);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity$initialize ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVerticalGravity get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalGravity {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDividerHeight {
    com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled get();
    com.netcetera.threeds.sdk.infrastructure.setHorizontalGravity initialize();
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.Boolean ThreeDS2Service();
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled

-keep class com.netcetera.threeds.sdk.infrastructure.setOverscrollHeader {
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setEdgeEffectColor,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDivider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinWidth {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinHeight {
    java.text.DateFormat initialize;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinHeight {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinHeight {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service {
    com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service ThreeDS2ServiceInstance;    com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service initialize;    com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service ThreeDS2Service;    com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service get;    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinHeight$ThreeDS2Service {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable {
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintList {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintList {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize getSDKInfo(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize onCompleted(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize ConfigParameters(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize onError(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize addParam(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintList {
    void ThreeDS2Service();
    void initialize(com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode);
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintMode {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintBlendMode {
    void ThreeDS2Service(java.util.List);
    java.util.List ThreeDS2ServiceInstance();
    void initialize(java.util.List);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintBlendMode {
    java.lang.String get(java.util.List);
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setMin);
    java.util.List get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgress {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMax

-keep class com.netcetera.threeds.sdk.infrastructure.setMax$get {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setMax$get ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax$get initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax$get get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax$get ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax$get getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax$get getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMax initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMin {
    java.util.List get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMin$ThreeDS2Service {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMin$ThreeDS2Service initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setMin$ThreeDS2Service getWarnings(com.netcetera.threeds.sdk.infrastructure.setMax);
    com.netcetera.threeds.sdk.infrastructure.setMin$ThreeDS2Service ThreeDS2Service(java.util.List);
    com.netcetera.threeds.sdk.infrastructure.setMin initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setEdgeEffectColor,java.lang.String);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setEdgeEffectColor,java.lang.String,java.lang.String,java.lang.String,java.lang.String);
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setInterpolator {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMovementMethod

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMovementMethod {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTypeface {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTransformationMethod {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setKeyListener {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map,com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelative {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablePadding {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintList {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelativeWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextSelectHandleLeft {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextCursorDrawable {
    com.netcetera.threeds.sdk.api.ThreeDS2Service get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextCursorDrawable {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextLocales

-keep class com.netcetera.threeds.sdk.infrastructure.setTextSize {
    java.lang.String ThreeDS2Service(java.security.KeyPair);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLetterSpacing {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setShiftDrawingOffsetForStartOverhang {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinimumFontMetrics {
    void get();
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams

-keep class com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency {
    java.lang.String getWarnings();
    java.lang.String get();
    java.security.PublicKey ThreeDS2Service();
    java.security.KeyPair initialize();
    java.lang.String cleanup();
    void ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBreakStrategy {
    void get(com.netcetera.threeds.sdk.infrastructure.setExtractedText);
    void ThreeDS2Service();
    void ThreeDS2ServiceInstance();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFontFeatureSettings {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setShadowLayer {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHighlightColor {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPaintFlags {
    com.netcetera.threeds.sdk.infrastructure.setMinimumFontMetrics initialize(com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoLinkMask {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLinksClickable {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHintTextColor {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLinkTextColor {
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeight {
    java.lang.String ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setLines);
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLines {
    com.netcetera.threeds.sdk.infrastructure.getClass get();
    java.lang.String getWarnings();
    java.lang.String ThreeDS2Service();
    java.lang.String cleanup();
    com.netcetera.threeds.sdk.infrastructure.getVersion createTransaction();
    com.netcetera.threeds.sdk.infrastructure.addSuppressed getSDKInfo();
    java.lang.String ConfigParameters();
    com.netcetera.threeds.sdk.infrastructure.clear apiKey();
    java.lang.Boolean configureScheme();
    com.netcetera.threeds.sdk.infrastructure.notify visaSchemeConfiguration();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLines {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxLines {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxLines {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinLines {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinLines {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEms {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWidth {
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFocusedSearchResultIndex {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setFilters);
    void get(com.netcetera.threeds.sdk.infrastructure.setExtractedText);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHighlights {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlights {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLineHeight {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFilters

-keep class com.netcetera.threeds.sdk.infrastructure.setExtractedText

-keep class com.netcetera.threeds.sdk.infrastructure.setAllCaps

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScroller {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScroller {
    void getSDKInfo();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScroller$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScroller$ThreeDS2Service {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScroller$ThreeDS2ServiceInstance {
    void initialize();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCursorVisible {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLogoDescription {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitleTextAppearance$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleTextAppearance {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitle {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitle {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleTextColor {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitleTextColor {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationOnClickListener {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationOnClickListener {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationContentDescription {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationContentDescription {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setContentInsetsAbsolute {
    <init>(java.lang.Runnable);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetsRelative {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    com.netcetera.threeds.sdk.infrastructure.setCollapseIcon ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency,com.netcetera.threeds.sdk.infrastructure.fy,com.netcetera.threeds.sdk.infrastructure.setWidth,com.netcetera.threeds.sdk.infrastructure.nc,com.netcetera.threeds.sdk.infrastructure.iz,com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize);
    void ThreeDS2Service(java.lang.String,com.netcetera.threeds.sdk.infrastructure.setFocusedSearchResultIndex);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetStartWithNavigation {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapsible {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapsible {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.findFragmentByTag {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.findFragmentById {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    java.lang.Object[] ThreeDS2Service(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.computeValue {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getView {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$4 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$2 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$2 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$1 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$3 {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.equals$5 {
    void getWarnings(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$5 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$8 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clone {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$3 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$3 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$4 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$1 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$2 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$6 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$8 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$9 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$9 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$7 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$7 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$10 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$13 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$12 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$12 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$20 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$18 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$16 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$16 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$19 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$25 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$23 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.finalize {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.finalize {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.finalize$getWarnings {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.finalize$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.notify

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.notify {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getClass {
    com.netcetera.threeds.sdk.infrastructure.getClass get;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCause {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getLocalizedMessage {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.wait {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.notifyAll {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.initCause {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.printStackTrace {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.addSuppressed

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.addSuppressed {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStackTrace {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.fillInStackTrace {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getStackTrace {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getSuppressed {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getVersion {
    com.netcetera.threeds.sdk.infrastructure.getVersion initialize;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getVersion {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.load {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.clear

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clear {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getInfo {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putAll {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.entrySet {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.remove

-keep class com.netcetera.threeds.sdk.infrastructure.replaceAll {
    void initialize();
    void get(com.netcetera.threeds.sdk.infrastructure.setFilters);
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.computeIfAbsent

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.keys$initialize {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getServices$get {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.contains {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.contains$ThreeDS2Service {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.containsKey {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.save {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.stringPropertyNames {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aa {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ab {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ae {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ag {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ag {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.af {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.al {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ai {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ai$getWarnings {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ak {
    void initialize(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ap {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ap$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.am {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.am {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.av {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ba {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ay {
    void getSDKInfo();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aw {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bb$1 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bd$ThreeDS2ServiceInstance {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bi {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bi$5 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bi$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bk {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bn {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bm {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bp {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bp {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bo {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bq {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bt {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bu {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bv {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cd {
    void ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ca {
    com.netcetera.threeds.sdk.infrastructure.replaceAll initialize(com.netcetera.threeds.sdk.infrastructure.computeIfAbsent);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ce {
    void init$0();
    void init$1();
    void init$2();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cq {
    void getWarnings(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cp {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ct {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.di {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ds {
    java.lang.Object[] get(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ds {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dt {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.dy {
    void ThreeDS2ServiceInstance(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.en {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.et {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.et$initialize {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.et$ThreeDS2Service {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.et$getWarnings {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.fa {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fb {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ex {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fe {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fd {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fh {
    com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.fy

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ga {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ii {
    boolean ThreeDS2ServiceInstance();
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ii {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ig {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ie {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ij {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales);
}

-keep class com.netcetera.threeds.sdk.infrastructure.in {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.remove);
}

-keep class com.netcetera.threeds.sdk.infrastructure.im {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setFilters);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ik {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setExtractedText);
}

-keep class com.netcetera.threeds.sdk.infrastructure.il {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setAllCaps);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ip {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ir {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.is {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales,com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iq {
    <init>(com.netcetera.threeds.sdk.infrastructure.setTextLocales);
}

-keep class com.netcetera.threeds.sdk.infrastructure.io {
    com.netcetera.threeds.sdk.infrastructure.setLines initialize();
    com.netcetera.threeds.sdk.infrastructure.setLines ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.remove);
    com.netcetera.threeds.sdk.infrastructure.setAllCaps get(com.netcetera.threeds.sdk.infrastructure.setOnCapturedPointerListener);
}

-keep class com.netcetera.threeds.sdk.infrastructure.it {
    com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.it {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.iv {
    com.netcetera.threeds.sdk.api.transaction.Transaction get(com.netcetera.threeds.sdk.infrastructure.getErrorComponent,com.netcetera.threeds.sdk.infrastructure.getTransactionStatus,com.netcetera.threeds.sdk.infrastructure.setScrollBarSize,com.netcetera.threeds.sdk.infrastructure.lx,com.netcetera.threeds.sdk.infrastructure.jo,com.netcetera.threeds.sdk.infrastructure.setFriction$getWarnings,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.nc,com.netcetera.threeds.sdk.infrastructure.setProgressTintList,com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintBlendMode$initialize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iz

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ja {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.je {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jh {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jj {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.jo

-keep class com.netcetera.threeds.sdk.infrastructure.jr {
    com.netcetera.threeds.sdk.infrastructure.jo ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization,com.netcetera.threeds.sdk.infrastructure.lx);
    com.netcetera.threeds.sdk.infrastructure.jo getWarnings(java.util.Map,com.netcetera.threeds.sdk.infrastructure.lx);
}

-keep class com.netcetera.threeds.sdk.infrastructure.js {
    com.netcetera.threeds.sdk.api.ui.logic.UiCustomization ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
    java.util.Map ThreeDS2Service(java.util.Map);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.js {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.js {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jw {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jw {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jy {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jy {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ka {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ka {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jx {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kf {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kf {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kd {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kd {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kg {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kg {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ki {
    void get(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ki {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kk {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ln {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.lx

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.md {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mb {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mf {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mn {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mo {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mk$initialize {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ml {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mt {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mt {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ms {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mq {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.mp {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.my {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.my$ThreeDS2ServiceInstance

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mv {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.mw

-keep class com.netcetera.threeds.sdk.infrastructure.mw$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.mw$ThreeDS2ServiceInstance initialize;    com.netcetera.threeds.sdk.infrastructure.mw$ThreeDS2ServiceInstance ThreeDS2Service;    com.netcetera.threeds.sdk.infrastructure.mw$ThreeDS2ServiceInstance ThreeDS2ServiceInstance;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mw$ThreeDS2ServiceInstance {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.na {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nd {
    <init>(java.lang.Object);
    java.lang.String get(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nd {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nb {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nb {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nc {
    com.netcetera.threeds.sdk.infrastructure.nc initialize(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nc {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nf {
    void ThreeDS2ServiceInstance(java.lang.Object,java.lang.String);
    java.lang.String getWarnings(java.lang.String,java.lang.String);
    void ThreeDS2ServiceInstance(int,int,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nf {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ni {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ne {
    <init>();
    void ThreeDS2ServiceInstance(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ng {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ng {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nj {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nj {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nm {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nr {
    com.netcetera.threeds.sdk.infrastructure.nr ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nr {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.no {
    void get(java.lang.String);
    void getWarnings(java.lang.String,java.lang.Object[]);
    void initialize(java.lang.String);
    void ThreeDS2Service(java.lang.String,java.lang.Object[]);
    void getWarnings(java.lang.String,com.netcetera.threeds.sdk.infrastructure.nc);
}

-keep class com.netcetera.threeds.sdk.infrastructure.nq {
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nq {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.np {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ob {
    java.lang.String[] getWarnings(java.lang.Object[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ob {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.og {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oh {
    java.lang.CharSequence getWarnings(java.lang.CharSequence,java.lang.CharSequence);
    boolean ThreeDS2ServiceInstance(java.lang.CharSequence);
    boolean get(java.lang.CharSequence);
    boolean get(java.lang.CharSequence,java.lang.CharSequence);
    boolean ThreeDS2Service(java.lang.CharSequence,java.lang.CharSequence);
    boolean initialize(java.lang.CharSequence);
    java.lang.String[] ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
    boolean ThreeDS2Service(java.lang.CharSequence[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.of {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oe {
    java.lang.Long getWarnings;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oe {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oi {
    java.util.Date initialize(java.util.Date,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oi {
    <init>();
}

-keep class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ok {
    java.lang.String get(byte[]);
    byte[] ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ok {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.om {
    <init>();
    byte[] ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String get(java.lang.String);
    java.lang.String initialize(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oj {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ol {
    java.lang.Object[] initialize(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ol {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ol$get {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oq {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.oq$ThreeDS2Service initialize();
    com.netcetera.threeds.sdk.infrastructure.oq$ThreeDS2Service getWarnings();
    java.security.SecureRandom ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oq$ThreeDS2Service {
    void get(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String ThreeDS2Service();
    void ThreeDS2Service(java.lang.String);
    java.lang.String initialize();
    void getWarnings(java.lang.String);
    void initialize(java.lang.String);
    java.lang.String getWarnings();
    void ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String cleanup();
    void ThreeDS2ServiceInitializationCallback(java.lang.String);
    java.lang.String createTransaction();
    void createTransaction(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oo {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oo$4 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oo$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.or {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.on {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ot {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ov {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pb {
    java.lang.String ThreeDS2Service();
    boolean getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pa {
    boolean ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pa {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oz {
    com.netcetera.threeds.sdk.infrastructure.oz ThreeDS2ServiceInstance;    <init>(com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance,java.lang.String[]);
    void ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance ThreeDS2Service;}

-keep class com.netcetera.threeds.sdk.infrastructure.pf {
    com.netcetera.threeds.sdk.infrastructure.pb getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.pe {
    com.netcetera.threeds.sdk.infrastructure.pe get();
    com.netcetera.threeds.sdk.infrastructure.pf ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.pf getWarnings();
    com.netcetera.threeds.sdk.infrastructure.pf initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pd {
    org.slf4j.Logger get;    java.lang.String ThreeDS2ServiceInstance;    java.lang.String initialize;    <init>();
    void ThreeDS2Service(java.lang.String);
    void get(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String ThreeDS2Service();
    void initialize(com.netcetera.threeds.sdk.infrastructure.rd);
    void getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qc {
    com.netcetera.threeds.sdk.infrastructure.qe getWarnings(java.security.spec.ECParameterSpec);
    com.netcetera.threeds.sdk.infrastructure.qe ThreeDS2Service(java.security.spec.ECParameterSpec,java.lang.String,java.security.SecureRandom);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qe {
    java.security.interfaces.ECPublicKey ThreeDS2Service();
    java.security.interfaces.ECPrivateKey initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qd {
    java.security.Key ThreeDS2ServiceInitializationCallback();
    void initialize(java.lang.String);
    java.lang.String cleanup();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qd$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.qd get(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qd$ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qk {
    java.security.PrivateKey onError();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qk$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.qk ThreeDS2Service(java.security.Key);
    com.netcetera.threeds.sdk.infrastructure.qk initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qk ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qk$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql$get {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qo {
    <init>();
    boolean getWarnings();
    java.lang.String getSDKVersion();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$getWarnings {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qm$get {
    void get(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm$getSDKInfo {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qs {
    java.lang.String ThreeDS2Service(java.lang.String[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qs {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qu {
    void ThreeDS2Service(java.lang.String,java.lang.String);
    void initialize(java.lang.String,java.lang.Object);
    void ThreeDS2ServiceInstance(java.lang.String,com.netcetera.threeds.sdk.infrastructure.qd);
    java.lang.String initialize(java.lang.String);
    java.lang.Long ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qk get(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qu {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qt {
    byte[] initialize;    <init>();
    void get(java.lang.String[]);
    void get(java.lang.String);
    java.lang.String onCompleted();
    void get(java.lang.String,java.lang.String);
    void getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qu onError();
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
    java.lang.String cleanup(java.lang.String);
    void createTransaction(java.lang.String);
    java.lang.String addParam();
    void ThreeDS2ServiceInitializationCallback(java.lang.String);
    java.security.Key getParamValue();
    void initialize(java.security.Key);
    byte[] restrictedParameters();
    void ThreeDS2Service(byte[]);
    boolean apiKey();
    com.netcetera.threeds.sdk.infrastructure.oz ConfigurationBuilder();
    void initialize(com.netcetera.threeds.sdk.infrastructure.oz);
    void configureScheme();
    com.netcetera.threeds.sdk.infrastructure.oq removeParam();
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.oq);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qq {
    void get(java.security.Key);
    java.security.Key get(java.security.Key,java.lang.Class);
    void ThreeDS2Service(java.security.Key);
    void ThreeDS2Service(java.security.Key,java.lang.String,int);
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qr {
    <init>(byte[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qr {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qw {
    <init>();
    boolean ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qy {
    java.security.spec.ECParameterSpec ThreeDS2ServiceInstance;    java.lang.String getWarnings(java.security.spec.EllipticCurve);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qy {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qv {
    <init>(byte[]);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rd {
    com.netcetera.threeds.sdk.infrastructure.rd get;    com.netcetera.threeds.sdk.infrastructure.rd ThreeDS2ServiceInstance;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.re {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ra {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rc {
    byte[] ThreeDS2Service;    byte[] initialize(int);
    byte[] ThreeDS2ServiceInstance(long);
    boolean getWarnings(byte[],byte[]);
    byte[] initialize(byte[][]);
    byte[] ThreeDS2ServiceInstance(byte[],int,int);
    byte[] ThreeDS2ServiceInstance(byte[]);
    byte[] getWarnings(byte[]);
    int initialize(byte[]);
    int getWarnings(int);
    int ThreeDS2ServiceInstance(int);
    byte[] ThreeDS2Service(int,java.security.SecureRandom);
    byte[] get(int);
    java.lang.String get(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rb {
    java.lang.String getWarnings(java.lang.Throwable);
    java.lang.String ThreeDS2ServiceInstance(java.lang.Throwable,java.lang.Class);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rb {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rg {
    java.security.MessageDigest getWarnings(java.lang.String);
    java.security.MessageDigest ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rg {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rj {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ri {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rh {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rf {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rk {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rl {
    java.lang.String initialize(byte[],java.lang.String);
    byte[] ThreeDS2ServiceInstance(java.lang.String);
    byte[] getWarnings(java.lang.String);
    byte[] getWarnings(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rn {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rm {
    javax.crypto.Mac initialize(java.lang.String,java.security.Key,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rm {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ro {
    byte[] initialize(byte[]);
    byte[] ThreeDS2Service(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rr {
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

-keepclassmembers,allowoptimization,allowobfuscation class org.greenrobot.eventbus.EventBus {
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
