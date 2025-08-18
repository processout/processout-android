


-keeppackagenames com.netcetera.threeds.sdk.api.info,com.netcetera.threeds.sdk.api,com.netcetera.threeds.sdk.api.configparameters,com.netcetera.threeds.sdk.api.ui.logic,com.netcetera.threeds.sdk.infrastructure,com.netcetera.threeds.sdk.api.security,com.netcetera.threeds.sdk.api.transaction.challenge,com.netcetera.threeds.sdk.api.exceptions,org.bouncycastle.jce.provider,com.netcetera.threeds.sdk.api.transaction,com.netcetera.threeds.sdk.api.ui,com.netcetera.threeds.sdk.api.transaction.challenge.events
-adaptresourcefilecontents !jni/arm64-v8a/libbefd.so,!jni/armeabi-v7a/libbefd.so,!jni/x86/libbefd.so,!jni/x86_64/libbefd.so,!lib/arm64-v8a/libbefd.so,!lib/armeabi-v7a/libbefd.so,!lib/x86/libbefd.so,!lib/x86_64/libbefd.so,dummyfile
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
    int getWarnings;    int initialize;    int ThreeDS2Service;    int ThreeDS2ServiceInstance;    long[] get;    long[] createTransaction;    short getSDKVersion;    byte[] cleanup;    int ThreeDS2ServiceInitializationCallback;    int getSDKInfo;    int ConfigParameters;    <init>(java.io.InputStream,int,int,short,int,int);
    <init>(java.io.InputStream,int,int,short,int,int,int,int);
    int read();
    int read(byte[],int,int);
    long skip(long);
    int available();
    boolean markSupported();
    void initialize();
    int getWarnings();
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.get {
    short get;    byte[] ThreeDS2Service;    byte[] getWarnings;    byte[] initialize;    int ThreeDS2ServiceInstance;    int getSDKInfo;    int getSDKVersion;    int createTransaction;    int ThreeDS2ServiceInitializationCallback;    int cleanup;    int getParamValue;    int onCompleted;    int onError;    int addParam;    int ConfigParameters;    int removeParam;    <init>(java.io.InputStream,int[],int,byte[],int,int);
    <init>(java.io.InputStream,int[],int,byte[],int,int,int,int);
    int read();
    int read(byte[],int,int);
    long skip(long);
    int available();
    boolean markSupported();
    void initialize(long,int);
    void getWarnings(long);
    void get();
    void initialize();
    int ThreeDS2Service();
    void ThreeDS2ServiceInstance();
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cleanup {
    int ThreeDS2Service;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ThreeDS2ServiceInitializationCallback {
    int ThreeDS2ServiceInstance;    int initialize;    int getWarnings;    <init>();
    void ThreeDS2ServiceInstance(int[]);
    int ThreeDS2Service(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKVersion {
    int ThreeDS2Service;    int initialize;    int getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.createTransaction {
    int get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKInfo {
    int ThreeDS2Service;    int getWarnings;    char get;    char ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onCompleted {
    int ThreeDS2Service;    char getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.addParam {
    int ThreeDS2ServiceInstance;    int ThreeDS2Service;    <init>();
    char[] get(long,char[],int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getParamValue {
    int get;    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onError {
    int ThreeDS2ServiceInstance;    int get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigParameters {
    int initialize;    char ThreeDS2Service;    char getWarnings;    int ThreeDS2ServiceInstance;    int get;    int ThreeDS2ServiceInitializationCallback;    int getSDKInfo;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.configureScheme {
    int initialize;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.removeParam {
    int get(java.lang.Object);
    int ThreeDS2Service(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.values {
    java.lang.Integer get;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.values {
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

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration {
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

-keep class com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions {
    <init>(com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$ThreeDS2ServiceInstance);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$ThreeDS2ServiceInstance ThreeDS2ServiceInstance(android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.set3DSServerTransactionID {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setAcsRefNumber);
    void ThreeDS2ServiceInstance(java.lang.Class);
    java.lang.Object get(java.lang.Class);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAcsRefNumber

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getAcsRefNumber {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ChallengeStatusReceiver {
    java.security.PublicKey ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getMessageVersionNumber {
    <init>(com.netcetera.threeds.sdk.infrastructure.showProgress,java.util.List,com.netcetera.threeds.sdk.infrastructure.setVelocityScale$ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.RuntimeErrorEvent {
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getErrorMessage {
    com.netcetera.threeds.sdk.infrastructure.RuntimeErrorEvent ThreeDS2Service(java.lang.String,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ProgressView {
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.getMessageVersionNumber);
}

-keep class com.netcetera.threeds.sdk.infrastructure.showProgress

-keep class com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent {
    void ThreeDS2Service(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.util.Locale);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings,com.netcetera.threeds.sdk.infrastructure.setVelocityScale$ThreeDS2Service,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.showProgress initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCornerRadius {
    void createTransaction();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCornerRadius$getWarnings {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.isProgressShown {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.hideProgress {
    java.lang.Object[] ThreeDS2Service(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hideProgress {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBorderWidth {
    void ThreeDS2Service(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getLabelCustomization {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ViewCustomization {
    java.lang.Object[] get(android.content.Context,int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ViewCustomization {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImportantForContentCapture {
    void ThreeDS2Service(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSystemGestureExclusionRects {
    java.lang.Object[] get(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSystemGestureExclusionRects {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTransitionAlpha {
    java.lang.Object[] initialize(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTransitionAlpha {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOutlineProvider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setOutlineProvider {
    void get();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollBarFadeDuration {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollBarFadeDuration {
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRenderEffect {
    com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled getSDKVersion();
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled {
    com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled$ThreeDS2ServiceInstance get();
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDrawingCacheEnabled$ThreeDS2ServiceInstance {
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerType {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDuplicateParentStateEnabled {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDuplicateParentStateEnabled$initialize {
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayerPaint {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLeftTopRightBottom {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPadding {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundGravity {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintMode {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setForegroundTintBlendMode {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTag {
    com.netcetera.threeds.sdk.infrastructure.setRenderEffect get(java.lang.String);
    java.util.List ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSelected {
    com.netcetera.threeds.sdk.infrastructure.setTag initialize(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPointerIcon {
    com.netcetera.threeds.sdk.infrastructure.ChallengeStatusReceiver ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSDKInfo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ThreeDS2ServiceInitializationCallback;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment onError;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getParamValue;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment addParam;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment onCompleted;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment mastercardSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment visaSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment build;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment jcbConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment eftposConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment unionSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeLogo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment newSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeId;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeName;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemePublicRootKeys;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSchemeEncryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeConfigurationBuilder;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment logoDark;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment encryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKNotInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment rootPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKRuntimeException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SDKAlreadyInitializedException;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment toString;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getErrorCode;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getCertPrefix;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment valueOf;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment SchemeInfo;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getIds;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getEncryptionCertificateKid;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment Warning;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSeverity;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getSDKAppID;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getMessageVersion;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getValue;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getProgressView;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment ChallengeParameters;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setAcsRefNumber;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment setThreeDSRequestorAppURL;    com.netcetera.threeds.sdk.infrastructure.setTextAlignment set3DSServerTransactionID;    com.netcetera.threeds.sdk.infrastructure.setTransitionName ThreeDS2ServiceInstance(java.lang.Object[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextAlignment {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNestedScrollingEnabled {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback$ThreeDS2Service);
    void get(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback$ThreeDS2Service,java.lang.Runnable);
    java.lang.Object ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback$get);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback$get

-keep class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback$ThreeDS2Service

-keep class com.netcetera.threeds.sdk.infrastructure.setTransitionName {
    com.netcetera.threeds.sdk.infrastructure.setTextAlignment getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIsCredential {
    <init>(com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTransitionGroup {
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTransitionGroup {
    void ThreeDS2Service();
    void init$0();
    void init$1();
    void init$2();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnHierarchyChangeListener {
    void ThreeDS2Service(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutTransition {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimation {
    void get(java.util.Date);
    com.netcetera.threeds.sdk.api.info.SDKInfo get(java.util.List);
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAnimationCacheEnabled {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache {
    java.util.Locale initialize(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAlwaysDrawnWithCacheEnabled {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAlwaysDrawnWithCacheEnabled {
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHttpAuthUsernamePassword {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling,com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.setCertificate);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarOverlay {
    void get(com.netcetera.threeds.sdk.infrastructure.setSingleLine);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setScroller);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCertificate {
    void getWarnings();
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay {
    java.lang.String ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable {
    com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable initialize();
    com.netcetera.threeds.sdk.infrastructure.setFindListener get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFindListener

-keep class com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist {
    com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay getWarnings();
    boolean initialize();
    int ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebViewRenderProcessClient {
    com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable getWarnings(java.lang.String,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setNetworkAvailable getWarnings(java.lang.String,java.lang.String,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebViewClient {
    com.netcetera.threeds.sdk.infrastructure.setWebViewRenderProcessClient get();
    com.netcetera.threeds.sdk.infrastructure.setWebViewRenderProcessClient getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDataDirectorySuffix {
    <init>(javax.net.ssl.SSLSocketFactory,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDataDirectorySuffix {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebChromeClient {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDownloadListener {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setItemChecked {
    java.lang.Integer ThreeDS2ServiceInstance;    java.lang.Integer initialize;    java.lang.Integer get;    android.util.Range getWarnings;}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle {
    <init>();
    boolean initialize(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setSafeBrowsingWhitelist);
    boolean get(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollAlwaysVisible {
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDrawSelectorOnTop {
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVelocityScale {
    com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setVelocityScale$ThreeDS2Service ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVelocityScale$ThreeDS2Service

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVelocityScale$ThreeDS2Service {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings initialize;    com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings getWarnings;    java.lang.String ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTranscriptMode {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint {
    java.lang.String initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter {
    java.lang.String ThreeDS2Service();
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings);
    java.lang.String getWarnings();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTopEdgeEffectColor {
    <init>();
    java.lang.String ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChecked {
    com.netcetera.threeds.sdk.infrastructure.setChecked get;}

-keep class com.netcetera.threeds.sdk.infrastructure.setChildIndicatorBoundsRelative {
    <init>(boolean);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageMatrix {
    <init>(java.lang.Object);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignBottomM24117$4 {
    int[] initialize;}

-keep class com.netcetera.threeds.sdk.infrastructure.setScaleType {
    com.netcetera.threeds.sdk.infrastructure.setScaleType getWarnings(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScaleType {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDividerDrawable {
    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setColorFilter {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageAlpha {
    com.netcetera.threeds.sdk.infrastructure.setImageAlpha get(android.content.Context);
    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setImageAlpha {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMeasureWithLargestChildEnabled {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setDivider);
    com.netcetera.threeds.sdk.infrastructure.setDivider ThreeDS2ServiceInstance();
    void ThreeDS2Service();
    java.lang.Long initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAligned {
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled);
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setDivider);
    com.netcetera.threeds.sdk.infrastructure.setDivider ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWeightSum {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWeightSum {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBaselineAlignedChildIndex {
    java.lang.String ThreeDS2ServiceInstance();
    void initialize(com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVerticalGravity {
    void get(java.lang.String);
    boolean initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setGravity {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus

-keep class com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus$getWarnings {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus$getWarnings getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled

-keep class com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings initialize(com.netcetera.threeds.sdk.infrastructure.setItemsCanFocus);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled$getWarnings createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setHeaderDividersEnabled get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOverscrollHeader {
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDivider {
    com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setOverscrollHeader ThreeDS2ServiceInstance();
    java.lang.String get();
    java.lang.Boolean ThreeDS2Service();
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFooterDividersEnabled

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawable {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinWidth {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminate {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable {
    java.text.DateFormat ThreeDS2Service;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize {
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize ThreeDS2Service;    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize get;    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize ThreeDS2ServiceInstance;    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize getWarnings;    java.lang.String initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$initialize {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintList {
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintList {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance getSDKInfo(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance getParamValue(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance addParam(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance onError(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance onCompleted(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode ThreeDS2Service();
    java.lang.Object[] get$3c7515e(int,int,java.lang.Object,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList {
    void initialize();
    void get(com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode);
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintMode {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintList {
    void get(java.util.List);
    java.util.List initialize();
    void getWarnings(java.util.List);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMax {
    java.lang.String getWarnings(java.util.List);
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress);
    java.util.List get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMin {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMin {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress$get {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setProgress$get get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress$get createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgress ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress {
    java.util.List get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress$initialize {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress$initialize ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress$initialize ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setProgress);
    com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress$initialize ThreeDS2Service(java.util.List);
    com.netcetera.threeds.sdk.infrastructure.setSecondaryProgress get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFillViewport {
    void get(com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter,java.lang.String);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter,java.lang.String,java.lang.String,java.lang.String,java.lang.String);
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithPresetSizes {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithConfiguration {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setKeyListener

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setKeyListener {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawables {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablePadding {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map,com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelativeWithIntrinsicBounds {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelative {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintList {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintBlendMode {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLastBaselineToBottomHeight {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextCursorDrawable {
    com.netcetera.threeds.sdk.api.ThreeDS2Service initialize();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextCursorDrawable {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextScaleX {
    com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters initialize(com.netcetera.threeds.sdk.infrastructure.getMessageVersionNumber,java.lang.String,java.security.KeyPair,com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinimumFontMetrics {
    java.lang.String getWarnings(java.security.KeyPair);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLetterSpacing {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setUseBoundsForWidth {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setBreakStrategy {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams {
    void getWarnings();
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineBreakStyle {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle {
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String getWarnings();
    java.security.PublicKey ThreeDS2Service();
    java.security.KeyPair initialize();
    java.lang.String getSDKInfo();
    void ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency {
    void initialize(com.netcetera.threeds.sdk.infrastructure.setAllCaps);
    void get();
    void getWarnings();
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setScroller);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setJustificationMode {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setShadowLayer {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPaintFlags {
    com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setLineBreakStyle);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHintTextColor {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLines {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinLines {
    java.lang.String initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeight {
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling);
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    com.netcetera.threeds.sdk.infrastructure.initCause initialize();
    java.lang.String get();
    java.lang.String ThreeDS2Service();
    java.lang.String ThreeDS2ServiceInitializationCallback();
    com.netcetera.threeds.sdk.infrastructure.clear createTransaction();
    com.netcetera.threeds.sdk.infrastructure.printStackTrace getSDKInfo();
    java.lang.String getParamValue();
    com.netcetera.threeds.sdk.infrastructure.keySet ConfigurationBuilder();
    java.lang.Boolean apiKey();
    com.netcetera.threeds.sdk.infrastructure.notify build();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMaxLines

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxLines {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxLines {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinEms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinEms {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxEms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMaxEms {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEms {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEms {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWidth {
    java.lang.String get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFocusedSearchResultIndex

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlights {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSingleLine

-keep class com.netcetera.threeds.sdk.infrastructure.setAllCaps

-keep class com.netcetera.threeds.sdk.infrastructure.setScroller

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart {
    void cleanup();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$initialize {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$initialize {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginStart$ThreeDS2ServiceInstance {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCustomSelectionActionModeCallback {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleMarginBottom {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitleTextColor {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTitle$get {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitleTextAppearance {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationOnClickListener {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationIcon {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSubtitleTextColor {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setNavigationContentDescription {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOverflowIcon {
    <init>(java.lang.Runnable);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetsRelative {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setContentInsetStartWithNavigation {
    com.netcetera.threeds.sdk.infrastructure.setContentInsetStartWithNavigation ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle,com.netcetera.threeds.sdk.infrastructure.fy,com.netcetera.threeds.sdk.infrastructure.setWidth,com.netcetera.threeds.sdk.infrastructure.na,com.netcetera.threeds.sdk.infrastructure.jc,com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance);
    void ThreeDS2Service(java.lang.String,com.netcetera.threeds.sdk.infrastructure.setFocusedSearchResultIndex);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetEndWithActions {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.findFragmentById {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.computeValue {
    java.lang.Object[] get(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.computeValue {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getView {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMenuCallbacks {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clone {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ordinal {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$1 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$5 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$2 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$2 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$4 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$3 {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.hashCode$6 {
    java.lang.Object[] ThreeDS2ServiceInstance(android.content.Context,int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$6 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hashCode$6 {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.name {
    void ThreeDS2Service(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$5 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$3 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$4 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$8 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$7 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$9 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$9 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$10 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$13 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$15 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$15 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$16 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$16 {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.equals$17 {
    void get(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$17 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$17 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$19 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$18 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.equals$25 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    void initialize();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass$ThreeDS2ServiceInstance {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getDeclaringClass {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.notify

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.notify {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.initCause {
    com.netcetera.threeds.sdk.infrastructure.initCause getWarnings;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.initCause {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.wait {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getLocalizedMessage {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCause {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.notifyAll {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.fillInStackTrace {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStackTrace {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.addSuppressed {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.printStackTrace

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.printStackTrace {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getStackTrace {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getSuppressed {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getVersion {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.load {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.clear {
    com.netcetera.threeds.sdk.infrastructure.clear initialize;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clear {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getInfo {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.keySet

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.keySet {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putAll {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putIfAbsent {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.entrySet {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.replace

-keep class com.netcetera.threeds.sdk.infrastructure.computeIfPresent {
    void get();
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setSingleLine);
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.keys {
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.replace);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.merge$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProperty$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.containsValue {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.containsValue$initialize {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.store {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ab {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aa {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.af {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ae {
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ad {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ad {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ac {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ai {
    void get(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ak {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ak$getWarnings {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ao {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ao$initialize {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.am {
    void ThreeDS2Service(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.am {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.au {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.az {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aw {
    void getSDKInfo();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bf {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bf$4 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bc {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bc$ThreeDS2ServiceInstance {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh {
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh$3 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bh$3 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bj {
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bp {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bn {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bo {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bu {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.br {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bt {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bx {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ce {
    void createTransaction();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.cb {
    com.netcetera.threeds.sdk.infrastructure.computeIfPresent get(com.netcetera.threeds.sdk.infrastructure.keys);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cc {
    void init$0();
    void init$1();
    void init$2();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cr {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cq {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.dm {
    java.lang.Object[] initialize(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dm {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dw {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eo {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eu {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eu$initialize {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eu$getWarnings {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eu$ThreeDS2ServiceInstance {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ev {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.fa {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ez {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ey {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fg {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ff {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.fl {
    com.netcetera.threeds.sdk.infrastructure.setMaxLines ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.fy

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ga {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.gd {
    void get(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ii {
    boolean initialize();
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ii {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ih {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ig {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed);
}

-keep class com.netcetera.threeds.sdk.infrastructure.im {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ik {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.replace);
}

-keep class com.netcetera.threeds.sdk.infrastructure.il {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setSingleLine);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ij {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setAllCaps);
}

-keep class com.netcetera.threeds.sdk.infrastructure.in {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.io {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ip {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.is {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed,com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iq {
    <init>(com.netcetera.threeds.sdk.infrastructure.setLocalePreferredLineHeightForMinimumUsed);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ir {
    com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling initialize(com.netcetera.threeds.sdk.infrastructure.replace);
    com.netcetera.threeds.sdk.infrastructure.setScroller ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setTransitionName);
}

-keep class com.netcetera.threeds.sdk.infrastructure.iw {
    com.netcetera.threeds.sdk.infrastructure.setCertificate get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.iw {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.iv {
    com.netcetera.threeds.sdk.api.transaction.Transaction ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.getMessageVersionNumber,com.netcetera.threeds.sdk.infrastructure.RuntimeErrorEvent,com.netcetera.threeds.sdk.infrastructure.setRenderEffect,com.netcetera.threeds.sdk.infrastructure.lv,com.netcetera.threeds.sdk.infrastructure.jn,com.netcetera.threeds.sdk.infrastructure.setVelocityScale$getWarnings,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.na,com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList,com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode$ThreeDS2ServiceInstance);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jc

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jb {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jd {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jk {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.jl {
    void initialize(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jn

-keep class com.netcetera.threeds.sdk.infrastructure.jo {
    com.netcetera.threeds.sdk.infrastructure.jn getWarnings(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization,com.netcetera.threeds.sdk.infrastructure.lv);
    com.netcetera.threeds.sdk.infrastructure.jn ThreeDS2Service(java.util.Map,com.netcetera.threeds.sdk.infrastructure.lv);
}

-keep class com.netcetera.threeds.sdk.infrastructure.js {
    com.netcetera.threeds.sdk.api.ui.logic.UiCustomization ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.ui.logic.UiCustomization);
    java.util.Map ThreeDS2ServiceInstance(java.util.Map);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.js {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jv {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ka {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jy {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jy {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jx {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kd {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kd {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kc {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kf {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kf {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ke {
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kk {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kl {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ki {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.lr {
    java.lang.Object[] ThreeDS2ServiceInstance(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lr {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.lv

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ma {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.me {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mg {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mk {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mm {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mn$ThreeDS2Service {
    void initialize();
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

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mp {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.mq {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.mu {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.mu$ThreeDS2Service

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mx {
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mw {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.mz {
    <init>(java.lang.Object);
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mz {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nd {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.na {
    com.netcetera.threeds.sdk.infrastructure.na ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.na {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nb {
    void getWarnings(java.lang.Object,java.lang.String);
    java.lang.String initialize(java.lang.String,java.lang.String);
    void getWarnings(int,int,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nh {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ng {
    <init>();
    void ThreeDS2ServiceInstance(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ne {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nk {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nk {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nm {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nn {
    com.netcetera.threeds.sdk.infrastructure.nn ThreeDS2Service(java.lang.String);
    java.lang.String initialize(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nn {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nr {
    void ThreeDS2Service(java.lang.String);
    void getWarnings(java.lang.String,java.lang.Object[]);
    void getWarnings(java.lang.String);
    void initialize(java.lang.String,java.lang.Object[]);
    void initialize(java.lang.String,com.netcetera.threeds.sdk.infrastructure.na);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ns {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ns {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.np {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oa {
    java.lang.String[] ThreeDS2Service(java.lang.Object[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oa {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.og {
    java.lang.CharSequence ThreeDS2ServiceInstance(java.lang.CharSequence,java.lang.CharSequence);
    boolean get(java.lang.CharSequence);
    boolean ThreeDS2Service(java.lang.CharSequence);
    boolean getWarnings(java.lang.CharSequence,java.lang.CharSequence);
    boolean get(java.lang.CharSequence,java.lang.CharSequence);
    boolean getWarnings(java.lang.CharSequence);
    java.lang.String[] initialize(java.lang.String,java.lang.String);
    boolean get(java.lang.CharSequence[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.og {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oe {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oh {
    java.lang.Long ThreeDS2ServiceInstance;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oh {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.od {
    java.util.Date getWarnings(java.util.Date,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.od {
    <init>();
}

-keep class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ol {
    java.lang.String ThreeDS2Service(byte[]);
    byte[] ThreeDS2ServiceInstance(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ol {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.om {
    <init>();
    byte[] initialize(java.lang.String);
    java.lang.String ThreeDS2Service(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oj {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ok$getWarnings {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oi {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.oi$initialize ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.oi$initialize getWarnings();
    java.security.SecureRandom initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oi$initialize {
    void getWarnings(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String getWarnings();
    void ThreeDS2Service(java.lang.String);
    java.lang.String ThreeDS2Service();
    void ThreeDS2ServiceInstance(java.lang.String);
    void initialize(java.lang.String);
    java.lang.String initialize();
    void get(java.lang.String);
    java.lang.String getSDKInfo();
    void cleanup(java.lang.String);
    java.lang.String createTransaction();
    void getSDKVersion(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.op {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.op$3 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.op$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.on {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.or {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ow {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.os {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pb {
    java.lang.String ThreeDS2ServiceInstance();
    boolean get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oy {
    boolean get(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oy {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oz {
    com.netcetera.threeds.sdk.infrastructure.oz get;    <init>(com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance,java.lang.String[]);
    void initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance {
    com.netcetera.threeds.sdk.infrastructure.oz$ThreeDS2ServiceInstance ThreeDS2ServiceInstance;}

-keep class com.netcetera.threeds.sdk.infrastructure.pa {
    com.netcetera.threeds.sdk.infrastructure.pb initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.pg {
    com.netcetera.threeds.sdk.infrastructure.pg initialize();
    com.netcetera.threeds.sdk.infrastructure.pa get();
    com.netcetera.threeds.sdk.infrastructure.pa ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.pa getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pd {
    org.slf4j.Logger ThreeDS2ServiceInstance;    <init>();
    void get(java.lang.String);
    void initialize(java.lang.String);
    java.lang.String getWarnings();
    java.lang.String ThreeDS2ServiceInstance();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.qy);
    void getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qc {
    com.netcetera.threeds.sdk.infrastructure.qb ThreeDS2Service(java.security.spec.ECParameterSpec);
    com.netcetera.threeds.sdk.infrastructure.qb getWarnings(java.security.spec.ECParameterSpec,java.lang.String,java.security.SecureRandom);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qb {
    java.security.interfaces.ECPublicKey ThreeDS2ServiceInstance();
    java.security.interfaces.ECPrivateKey ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qe {
    java.security.Key cleanup();
    void ThreeDS2Service(java.lang.String);
    java.lang.String ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qe$initialize {
    com.netcetera.threeds.sdk.infrastructure.qe getWarnings(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qe$initialize {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qj {
    java.security.PrivateKey addParam();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qj$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.qj getWarnings(java.security.Key);
    com.netcetera.threeds.sdk.infrastructure.qj getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qj ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qg$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qg$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qg$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qk$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qk$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qk$ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qm {
    <init>();
    boolean getWarnings();
    java.lang.String getSDKInfo();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qo {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qp$cleanup {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ql {
    java.lang.String getWarnings(java.lang.String[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ql {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qu {
    void get(java.lang.String,java.lang.String);
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.Object);
    void getWarnings(java.lang.String,com.netcetera.threeds.sdk.infrastructure.qe);
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
    java.lang.Long ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qj initialize(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qu {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qr {
    byte[] ThreeDS2ServiceInstance;    <init>();
    void ThreeDS2ServiceInstance(java.lang.String[]);
    void ThreeDS2Service(java.lang.String);
    java.lang.String ThreeDS2ServiceInitializationCallback();
    void getWarnings(java.lang.String,java.lang.String);
    void ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.qu ConfigParameters();
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
    java.lang.String createTransaction(java.lang.String);
    void getSDKInfo(java.lang.String);
    java.lang.String onCompleted();
    void ThreeDS2ServiceInitializationCallback(java.lang.String);
    java.security.Key getParamValue();
    void ThreeDS2Service(java.security.Key);
    byte[] addParam();
    void get(byte[]);
    boolean restrictedParameters();
    com.netcetera.threeds.sdk.infrastructure.oz apiKey();
    void initialize(com.netcetera.threeds.sdk.infrastructure.oz);
    void configureScheme();
    com.netcetera.threeds.sdk.infrastructure.oi ConfigurationBuilder();
    void get(com.netcetera.threeds.sdk.infrastructure.oi);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qs {
    void getWarnings(java.security.Key);
    void ThreeDS2Service(java.security.Key);
    void initialize(byte[],java.lang.String);
    void get(java.security.Key,java.lang.String,int);
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qs {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qs {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qt {
    <init>(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qq {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qw {
    <init>();
    boolean initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qz {
    java.security.spec.ECParameterSpec get;    java.lang.String initialize(java.security.spec.EllipticCurve);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qv {
    <init>(byte[]);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qy {
    com.netcetera.threeds.sdk.infrastructure.qy ThreeDS2Service;    com.netcetera.threeds.sdk.infrastructure.qy get;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.re {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ra {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rd {
    byte[] getWarnings;    byte[] getWarnings(int);
    byte[] ThreeDS2Service(long);
    boolean get(byte[],byte[]);
    byte[] initialize(byte[][]);
    byte[] ThreeDS2Service(byte[],int,int);
    byte[] initialize(byte[]);
    byte[] ThreeDS2Service(byte[]);
    int get(byte[]);
    int ThreeDS2Service(int);
    int get(int);
    byte[] getWarnings(int,java.security.SecureRandom);
    byte[] ThreeDS2ServiceInstance(int);
    java.lang.String getWarnings(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rd {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rb {
    java.lang.String ThreeDS2Service(java.lang.Throwable);
    java.lang.String getWarnings(java.lang.Throwable,java.lang.Class);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rb {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rc {
    java.security.MessageDigest initialize(java.lang.String);
    java.security.MessageDigest initialize(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rf {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rj {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rh {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rg {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ri {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rm {
    java.lang.String get(byte[],java.lang.String);
    byte[] getWarnings(java.lang.String);
    byte[] ThreeDS2ServiceInstance(java.lang.String);
    byte[] ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rm {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rn {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.rl {
    javax.crypto.Mac initialize(java.lang.String,java.security.Key,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rk {
    byte[] getWarnings(byte[]);
    byte[] ThreeDS2ServiceInstance(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ro {
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
