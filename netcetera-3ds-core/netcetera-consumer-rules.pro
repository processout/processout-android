


-keeppackagenames com.netcetera.threeds.sdk.api.info,com.netcetera.threeds.sdk.api,com.netcetera.threeds.sdk.api.configparameters,com.netcetera.threeds.sdk.api.ui.logic,com.netcetera.threeds.sdk.infrastructure,com.netcetera.threeds.sdk.api.security,com.netcetera.threeds.sdk.api.transaction.challenge,com.netcetera.threeds.sdk.api.exceptions,org.bouncycastle.jce.provider,com.netcetera.threeds.sdk.api.transaction,com.netcetera.threeds.sdk.api.ui,com.netcetera.threeds.sdk.api.transaction.challenge.events
-adaptresourcefilecontents !jni/arm64-v8a/libbdaf.so,!jni/armeabi-v7a/libbdaf.so,!jni/x86/libbdaf.so,!jni/x86_64/libbdaf.so,!lib/arm64-v8a/libbdaf.so,!lib/armeabi-v7a/libbdaf.so,!lib/x86/libbdaf.so,!lib/x86_64/libbdaf.so,dummyfile
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
    int ThreeDS2Service;    int initialize;    int ThreeDS2ServiceInstance;    int get;    long[] getWarnings;    long[] getSDKInfo;    short getSDKVersion;    byte[] cleanup;    int createTransaction;    int ThreeDS2ServiceInitializationCallback;    int ConfigParameters;    <init>(java.io.InputStream,int,int,short,int,int);
    <init>(java.io.InputStream,int,int,short,int,int,int,int);
    int read();
    int read(byte[],int,int);
    long skip(long);
    int available();
    boolean markSupported();
    void ThreeDS2Service();
    int get();
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.initialize {
    byte[] ThreeDS2ServiceInstance;    int[] get;    int[] initialize;    int[] getWarnings;    int[] ThreeDS2Service;    int ThreeDS2ServiceInitializationCallback;    int[] getSDKVersion;    byte[][] createTransaction;    int[] getSDKInfo;    byte[] cleanup;    byte[] onError;    int getParamValue;    int onCompleted;    int ConfigParameters;    int addParam;    int configureScheme;    int removeParam;    <init>(java.io.InputStream,int,byte[],byte[][]);
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
    byte[][] get(byte[][]);
    int ThreeDS2Service();
    void getWarnings(byte[],int,byte[],int);
    void get();
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.createTransaction {
    int ThreeDS2ServiceInstance;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSDKVersion {
    int get;    int initialize;    int ThreeDS2Service;    <init>();
    void initialize(int[]);
    int ThreeDS2ServiceInstance(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.cleanup {
    int ThreeDS2ServiceInstance;    int ThreeDS2Service;    int get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.addParam {
    int ThreeDS2Service;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.onCompleted {
    int get;    int getWarnings;    char ThreeDS2ServiceInstance;    char ThreeDS2Service;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getParamValue {
    int get;    char getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ConfigParameters {
    int ThreeDS2ServiceInstance;    int getWarnings;    <init>();
    char[] getWarnings(long,char[],int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.onError {
    int get;    int getWarnings;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.configureScheme {
    int getWarnings;    int get;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.restrictedParameters {
    int ThreeDS2Service;    char getWarnings;    char ThreeDS2ServiceInstance;    int initialize;    int get;    int createTransaction;    int ThreeDS2ServiceInitializationCallback;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.removeParam {
    int ThreeDS2Service;    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.apiKey {
    int get(java.lang.Object);
    int get(int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.SDKInfo {
    java.lang.Integer get;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.SDKInfo {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.ThreeDS2ServiceInstance {
    <init>();
}

-keep class com.netcetera.threeds.sdk.api.ThreeDS2Service {
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

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration {
    void get();
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

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getSchemeConfigurations {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions {
    <init>(com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$get);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$get {
    com.netcetera.threeds.sdk.infrastructure.getSupportedProtocolVersions$get ThreeDS2ServiceInstance(android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.set3DSServerTransactionID {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.getAcsRefNumber);
    void ThreeDS2ServiceInstance(java.lang.Class);
    java.lang.Object ThreeDS2Service(java.lang.Class);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getAcsRefNumber

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getAcsTransactionID {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getTransactionID {
    java.security.PublicKey getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.api.info.CertificateInfo$CertificateType ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.showProgress {
    <init>(com.netcetera.threeds.sdk.infrastructure.isProgressShown,java.util.List,com.netcetera.threeds.sdk.infrastructure.setChecked$initialize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent {
    java.lang.String ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCornerRadius {
    com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent ThreeDS2ServiceInstance(java.lang.String,java.lang.String,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ButtonCustomization {
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.showProgress);
}

-keep class com.netcetera.threeds.sdk.infrastructure.isProgressShown

-keep class com.netcetera.threeds.sdk.infrastructure.getCornerRadius {
    void get(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.util.Locale);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings,com.netcetera.threeds.sdk.infrastructure.setChecked$initialize,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.isProgressShown initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hideProgress {
    void ThreeDS2ServiceInitializationCallback();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hideProgress$ThreeDS2ServiceInstance {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getTextFontSize {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.Customization {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getTextColor {
    void get(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.getKeyInfoConverter {
    java.lang.Object[] initialize$29a33360(int,int,java.lang.Object,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getKeyInfoConverter {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHandwritingDelegateFlags {
    void get(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarTrackDrawable {
    java.lang.Object[] get(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHorizontalScrollbarTrackDrawable {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollCaptureCallback {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollY {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setScrollY {
    void initialize();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSoundEffectsEnabled {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSoundEffectsEnabled {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextDirection {
    com.netcetera.threeds.sdk.infrastructure.setTooltipText ThreeDS2ServiceInitializationCallback();
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTooltipText {
    com.netcetera.threeds.sdk.infrastructure.setTooltipText$get ThreeDS2ServiceInstance();
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTooltipText$get {
    java.lang.String ThreeDS2Service();
    java.lang.String getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTag {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTop {
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTop$getWarnings {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTransitionAlpha {
    java.lang.Object getWarnings(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTransitionVisibility {
    java.lang.Object initialize(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarTrackDrawable {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarPosition {
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setY {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWindowInsetsAnimationCallback {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWillNotDraw {
    com.netcetera.threeds.sdk.infrastructure.setTextDirection initialize(java.lang.String);
    java.util.List get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setX {
    com.netcetera.threeds.sdk.infrastructure.setWillNotDraw get(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDescendantFocusability {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLayoutAnimation {
    com.netcetera.threeds.sdk.infrastructure.getTransactionID get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache {
    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache cleanup;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache createTransaction;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache onError;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache addParam;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getParamValue;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache ConfigParameters;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache amexConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache SchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache mastercardSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache build;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache eftposConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache jcbConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache unionSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemeLogoDark;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemeName;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache newSchemeConfiguration;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemeEncryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache SchemeConfigurationBuilder;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemePublicRootKeys;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemeEncryptionPublicKeyId;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache encryptionPublicKey;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache encryptionPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache logoDark;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache rootPublicKey;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache logo;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache SDKRuntimeException;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache rootPublicKeyFromAssetCertificate;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache SDKAlreadyInitializedException;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getErrorCode;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache toString;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache CertificateInfoCertificateType;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getExpiryDate;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSchemeConfigurations;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache Severity;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getRootCertificates;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getMessage;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getId;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getSDKReferenceNumber;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache useBridgingExtension;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getValue;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache close;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache getProgressView;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache setThreeDSRequestorAppURL;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache setAcsSignedContent;    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache setAcsTransactionID;    com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus initialize(java.lang.Object[]);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLayoutTransition {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled {
    void get(com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled$ThreeDS2Service);
    void get(com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled$ThreeDS2Service,java.lang.Runnable);
    java.lang.Object ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled$getWarnings

-keep class com.netcetera.threeds.sdk.infrastructure.setMotionEventSplittingEnabled$ThreeDS2Service

-keep class com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus {
    com.netcetera.threeds.sdk.infrastructure.setPersistentDrawingCache initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFindListener {
    <init>(com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver);
    void ThreeDS2Service(com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent);
    void ThreeDS2ServiceInstance();
    void initialize();
    void ThreeDS2Service(com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent);
    void <clinit>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setInitialScale {
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setInitialScale {
    void get();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setVerticalScrollbarOverlay {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setPictureListener {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAdapter {
    void getWarnings(java.util.Date);
    com.netcetera.threeds.sdk.api.info.SDKInfo getWarnings(java.util.List);
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWebChromeClient {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWebViewRenderProcessClient {
    void initialize(long,long);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setWebViewRenderProcessClient {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEdgeEffectColor {
    java.util.Locale ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDrawSelectorOnTop {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setDrawSelectorOnTop {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChoiceMode {
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription,com.netcetera.threeds.sdk.infrastructure.setCacheColorHint);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.finalize,com.netcetera.threeds.sdk.infrastructure.setBottomEdgeEffectColor);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCacheColorHint {
    void get(com.netcetera.threeds.sdk.infrastructure.getView);
    void initialize(com.netcetera.threeds.sdk.infrastructure.setCollapsible);
    void initialize(com.netcetera.threeds.sdk.infrastructure.finalize);
    void get(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setBottomEdgeEffectColor {
    void ThreeDS2ServiceInstance();
    void initialize(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollAlwaysVisible {
    java.lang.String ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle {
    com.netcetera.threeds.sdk.infrastructure.setFastScrollEnabled getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.setFilterText ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFilterText

-keep class com.netcetera.threeds.sdk.infrastructure.setFastScrollEnabled {
    com.netcetera.threeds.sdk.infrastructure.setFastScrollAlwaysVisible get();
    boolean getWarnings();
    int ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter {
    com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle ThreeDS2ServiceInstance(java.lang.String,java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setFastScrollStyle ThreeDS2Service(java.lang.String,java.lang.String,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setRecyclerListener {
    com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter get();
    com.netcetera.threeds.sdk.infrastructure.setRemoteViewsAdapter initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOnScrollListener {
    <init>(javax.net.ssl.SSLSocketFactory,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSelector {
    java.lang.Object get(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSmoothScrollbarEnabled {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setVelocityScale {
    java.lang.Integer getWarnings;    java.lang.Integer ThreeDS2ServiceInstance;    java.lang.Integer get;    android.util.Range initialize;}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextFilterEnabled {
    <init>();
    boolean get(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
    java.lang.String get(com.netcetera.threeds.sdk.infrastructure.setFastScrollEnabled);
    boolean initialize(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTopEdgeEffectColor {
    com.netcetera.threeds.sdk.infrastructure.setChoiceMode get(com.netcetera.threeds.sdk.infrastructure.setChoiceMode);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEllipsize {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChecked {
    com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setChecked$initialize ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChecked$initialize

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setChecked$initialize {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings {
    com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings getWarnings;    com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings get;    java.lang.String getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setText {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setGroupIndicator {
    java.lang.String initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChildIndicatorBounds {
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings);
    java.lang.String ThreeDS2Service();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setChildIndicatorBoundsRelative {
    <init>();
    java.lang.String get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndicatorBounds {
    com.netcetera.threeds.sdk.infrastructure.setIndicatorBounds getWarnings;}

-keep class com.netcetera.threeds.sdk.infrastructure.setImageTintMode {
    <init>(boolean);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setDividerHeight {
    <init>(java.lang.Object);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawableTiledH2708$1 {
    int[] ThreeDS2ServiceInstance;}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminate {
    com.netcetera.threeds.sdk.infrastructure.setIndeterminate ThreeDS2ServiceInstance(java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminate {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setOverscrollHeader {
    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setIndeterminateDrawable {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setInterpolator {
    com.netcetera.threeds.sdk.infrastructure.setInterpolator getWarnings(android.content.Context);
    java.lang.String initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setInterpolator {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMax {
    void get(com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList initialize();
    void ThreeDS2ServiceInstance();
    java.lang.Long ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setIndeterminateTintMode {
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled);
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList);
    com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList getWarnings(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgress {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgress {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintBlendMode {
    java.lang.String ThreeDS2ServiceInstance();
    void get(com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintList);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinHeight {
    void getWarnings(java.lang.String);
    boolean getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintMode {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$ThreeDS2Service {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$ThreeDS2Service ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable$ThreeDS2Service ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawable get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setProgressDrawable);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled$getWarnings ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setProgressDrawableTiled ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode {
    java.lang.String ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList {
    com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintList getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setProgressTintBlendMode ThreeDS2Service();
    java.lang.String initialize();
    java.lang.Boolean ThreeDS2ServiceInstance();
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintList

-keep class com.netcetera.threeds.sdk.infrastructure.setProgressTintMode {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setChildIndicatorBounds,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProgressTintList {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSecondaryProgressTintBlendMode {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFillViewport {
    java.text.DateFormat getWarnings;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFillViewport {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFillViewport {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service {
    com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service get;    com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service initialize;    com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service getWarnings;    com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service ThreeDS2Service;    java.lang.String ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setFillViewport$ThreeDS2Service {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablePadding {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithConfiguration {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults

-keep class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service ThreeDS2ServiceInitializationCallback(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service createTransaction(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service getSDKInfo(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service onCompleted(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service getParamValue(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service onError(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service ConfigParameters(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithPresetSizes {
    void get();
    void get(com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service);
    void ThreeDS2Service();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawableTintList {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawables {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCustomSelectionActionModeCallback {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCursorVisible {
    void get(java.util.List);
    java.util.List ThreeDS2Service();
    void ThreeDS2ServiceInstance(java.util.List);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesRelativeWithIntrinsicBounds {
    java.lang.String getWarnings(java.util.List);
    java.lang.String ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setEditableFactory);
    java.util.List ThreeDS2Service(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesWithIntrinsicBounds {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCompoundDrawablesWithIntrinsicBounds {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEms {
    java.lang.String get();
    java.lang.String getWarnings();
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String ThreeDS2Service();
    java.lang.String initialize();
    java.lang.String ThreeDS2ServiceInitializationCallback();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEms$initialize {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms$initialize getSDKInfo(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEms get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight

-keep class com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get ThreeDS2Service(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get get(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get cleanup(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight$get initialize(java.util.Map);
    com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEditableFactory {
    java.util.List getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setEditableFactory$ThreeDS2Service {
    <init>(java.lang.String,java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEditableFactory$ThreeDS2Service getWarnings(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.setEditableFactory$ThreeDS2Service ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.setElegantTextHeight);
    com.netcetera.threeds.sdk.infrastructure.setEditableFactory$ThreeDS2Service getWarnings(java.util.List);
    com.netcetera.threeds.sdk.infrastructure.setEditableFactory initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setEditableFactory$ThreeDS2Service {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setError {
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setChildIndicatorBounds,java.lang.String);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setEms);
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setExtractedText {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHighlightColor

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setHighlightColor {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHeight {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHighlights {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.lang.String,java.util.Map,com.netcetera.threeds.sdk.api.ThreeDS2Service$InitializationCallback);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setFontFeatureSettings {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImeActionLabel {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHintTextColor {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHorizontallyScrolling {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,android.content.Context);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHyphenationFrequency {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setHint {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String,java.lang.String,java.util.Map);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImeOptions {
    <init>(java.lang.Object);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setImeOptions {
    void init$0();
    void init$1();
    void init$2();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setInputType {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setInputExtras {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setImeHintLocales {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters,java.util.Locale);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLetterSpacing {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.setTextDirection,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setJustificationMode {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings,com.netcetera.threeds.sdk.infrastructure.setChecked$initialize,java.lang.String,com.netcetera.threeds.sdk.infrastructure.jv);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineBreakStyle {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.setChecked$initialize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setKeyListener {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.showProgress,com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent,com.netcetera.threeds.sdk.infrastructure.setTextDirection,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings,java.lang.String,com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.setEms);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLastBaselineToBottomHeight {
    <init>(java.lang.Object);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLinkTextColor {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLines {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setLineBreakWordStyle {
    <init>(com.netcetera.threeds.sdk.api.ThreeDS2Service,java.util.Map);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMovementMethod {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setMinimumFontMetrics {
    com.netcetera.threeds.sdk.api.ThreeDS2Service initialize();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setMinimumFontMetrics {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlightColor {
    com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.showProgress,java.lang.String,java.security.KeyPair,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setScroller {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setShiftDrawingOffsetForStartOverhang {
    java.lang.String ThreeDS2Service(java.security.KeyPair);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSelectAllOnFocus {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlights {
    void getWarnings();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setShowSoftInputOnFocus {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextIsSelectable {
    void get();
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextAppearance {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setSpannableFactory {
    java.lang.String get();
    java.lang.String getWarnings();
    java.security.PublicKey ThreeDS2Service();
    java.security.KeyPair initialize();
    java.lang.String createTransaction();
    void getSDKInfo();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextCursorDrawable {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setCollapsible);
    void getWarnings();
    void ThreeDS2Service();
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.finalize);
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextMetricsParams {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextLocale {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextScaleX {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextLocales {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setTextSelectHandle {
    com.netcetera.threeds.sdk.infrastructure.setTextIsSelectable getWarnings(com.netcetera.threeds.sdk.infrastructure.setTextAppearance);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTextSelectHandleLeft {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTransformationMethod {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setWidth {
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCollapseIcon {
    java.lang.String getWarnings(com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription);
    java.lang.String initialize(com.netcetera.threeds.sdk.infrastructure.finalize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    com.netcetera.threeds.sdk.infrastructure.size ThreeDS2Service();
    java.lang.String initialize();
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String getSDKInfo();
    com.netcetera.threeds.sdk.infrastructure.ac getSDKVersion();
    com.netcetera.threeds.sdk.infrastructure.store createTransaction();
    java.lang.String getParamValue();
    com.netcetera.threeds.sdk.infrastructure.ae ConfigurationBuilder();
    java.lang.Boolean restrictedParameters();
    com.netcetera.threeds.sdk.infrastructure.containsKey mastercardSchemeConfiguration();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setUseBoundsForWidth

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setUseBoundsForWidth {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setUseBoundsForWidth {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTypeface {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setTypeface {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetsRelative {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setContentInsetsRelative {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLogo {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLogo {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.setContentInsetsAbsolute {
    java.lang.String get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.setNavigationIcon {
    void get(com.netcetera.threeds.sdk.infrastructure.getView);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setCollapsible);
    void ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.finalize);
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setLogoDescription {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getView

-keep class com.netcetera.threeds.sdk.infrastructure.setCollapsible

-keep class com.netcetera.threeds.sdk.infrastructure.finalize

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo {
    void getSDKInfo();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo$getWarnings {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compareTo$initialize {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getClass {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.name {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getLocalizedMessage {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getStackTrace$getWarnings {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getSuppressed {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.initCause {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getCause {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setStackTrace {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.computeIfAbsent {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.computeIfAbsent {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.clear {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.printStackTrace {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.printStackTrace {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compute {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.compute {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.elements {
    <init>(java.lang.Runnable);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.entrySet {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.getVersion {
    com.netcetera.threeds.sdk.infrastructure.getVersion initialize(com.netcetera.threeds.sdk.infrastructure.setSpannableFactory,com.netcetera.threeds.sdk.infrastructure.hs,com.netcetera.threeds.sdk.infrastructure.setContentInsetsAbsolute,com.netcetera.threeds.sdk.infrastructure.ot,com.netcetera.threeds.sdk.infrastructure.kv,com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service);
    void ThreeDS2ServiceInstance(java.lang.String,com.netcetera.threeds.sdk.infrastructure.setNavigationIcon);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getVersion {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getServices {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getOrDefault {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.getOrDefault {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.load {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.put {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.keys {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.merge {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.putIfAbsent {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.replace$3 {
    void ThreeDS2Service(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$3 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$3 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$1 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$2 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$2 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$4 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$5 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.replace$9 {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.replace$8 {
    void get(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.writeReplace {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove {
    void getSDKVersion();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$1 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$4 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$3 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$7 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$6 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$8 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$8 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$9 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$10 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$15 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$15 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$11 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$12 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$12 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$16 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$16 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$17 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$20 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$22 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$22 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.remove$25 {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.isEmpty {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.isEmpty {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.isEmpty$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.isEmpty$ThreeDS2ServiceInstance {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.containsValue {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.containsKey

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.containsKey {
    void ThreeDS2Service();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.size {
    com.netcetera.threeds.sdk.infrastructure.size initialize;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.size {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.loadFromXML {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.list {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.save {
    void get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.propertyNames {
    void initialize();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.setProperty {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.stringPropertyNames {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.storeToXML {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.store

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.store {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.v {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aa {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.z {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ab {
    void get();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ac {
    com.netcetera.threeds.sdk.infrastructure.ac initialize;}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ac {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ad {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ae

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ae {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.af {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aj {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ak {
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.am

-keep class com.netcetera.threeds.sdk.infrastructure.an {
    void get();
    void ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.getView);
    void ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ap {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.am);
}

-keep class com.netcetera.threeds.sdk.infrastructure.aq$2 {
    java.lang.Object[] get(android.content.Context,int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aq$2 {
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.aq$initialize {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.az$ThreeDS2Service {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bd {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bd$ThreeDS2ServiceInstance {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ba {
    void ThreeDS2Service();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bg {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bk {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bm {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bn {
    void getWarnings();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bt {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bs {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bs {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.br {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bp {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bv {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bv$ThreeDS2ServiceInstance {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bx {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.bx$ThreeDS2Service {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cc {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cb {
    void get();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ci {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cl {
    void ThreeDS2ServiceInitializationCallback();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ck {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.co {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.co$5 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.co$5 {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cr {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cr$ThreeDS2ServiceInstance {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cq {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cq$1 {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cx {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.da {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dc {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.cz {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.db {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dh {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.de {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.dj {
    java.lang.Object[] get(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dj {
    void init$0();
    void init$1();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.do {
    void createTransaction();
}

-keep class com.netcetera.threeds.sdk.infrastructure.dn {
    com.netcetera.threeds.sdk.infrastructure.an getWarnings(com.netcetera.threeds.sdk.infrastructure.ap);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.dp {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.eg {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ed {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.eq {
    void ThreeDS2Service(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.en {
    void ThreeDS2ServiceInstance(long,long);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ew {
    void ThreeDS2ServiceInstance(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.fh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ga {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gd {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gd$getWarnings {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gd$initialize {
    void ThreeDS2Service();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gd$ThreeDS2ServiceInstance {
    void initialize();
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.gj {
    void getWarnings();
}

-keep class com.netcetera.threeds.sdk.infrastructure.go {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gl {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gn {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gk {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gq {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gt {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gr {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters,com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeStatusReceiver,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gw {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,com.netcetera.threeds.sdk.api.transaction.Transaction$BridgingMessageExtensionVersion);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gy {
    <init>(com.netcetera.threeds.sdk.api.transaction.Transaction,android.app.Activity);
}

-keep class com.netcetera.threeds.sdk.infrastructure.gx {
    com.netcetera.threeds.sdk.infrastructure.setUseBoundsForWidth ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.hs

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.hp {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.jv {
    <init>(java.util.Map);
    java.lang.String get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jv {
    void initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ka {
    <init>(com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlightColor,com.netcetera.threeds.sdk.infrastructure.showProgress);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jy {
    <init>(com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlightColor,java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jw {
    <init>(com.netcetera.threeds.sdk.infrastructure.setSearchResultHighlightColor,java.security.KeyPair);
}

-keep class com.netcetera.threeds.sdk.infrastructure.jx {
    boolean initialize();
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jx {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.jz {
    java.lang.Object[] initialize(int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.jz {
    void init$0();
    void init$1();
}

-keep class com.netcetera.threeds.sdk.infrastructure.kc {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ke {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kb {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kd {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.am);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ki {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.getView);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kj {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.setCollapsible);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kk {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.finalize);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kg {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kh {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kn {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller,com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ko {
    <init>(com.netcetera.threeds.sdk.infrastructure.setScroller);
}

-keep class com.netcetera.threeds.sdk.infrastructure.km {
    com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription getWarnings();
    com.netcetera.threeds.sdk.infrastructure.setCollapseContentDescription ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.infrastructure.am);
    com.netcetera.threeds.sdk.infrastructure.finalize initialize(com.netcetera.threeds.sdk.infrastructure.setTouchscreenBlocksFocus);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kq {
    com.netcetera.threeds.sdk.infrastructure.setBottomEdgeEffectColor get();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kq {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.kr {
    com.netcetera.threeds.sdk.api.transaction.Transaction initialize(com.netcetera.threeds.sdk.infrastructure.showProgress,com.netcetera.threeds.sdk.infrastructure.ProtocolErrorEvent,com.netcetera.threeds.sdk.infrastructure.setTextDirection,com.netcetera.threeds.sdk.infrastructure.nk,com.netcetera.threeds.sdk.infrastructure.lo,com.netcetera.threeds.sdk.infrastructure.setChecked$getWarnings,java.lang.String,com.netcetera.threeds.sdk.infrastructure.ot,com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeUniformWithPresetSizes,com.netcetera.threeds.sdk.infrastructure.setAutoSizeTextTypeWithDefaults$ThreeDS2Service,com.netcetera.threeds.sdk.infrastructure.pm,com.netcetera.threeds.sdk.infrastructure.setError,com.netcetera.threeds.sdk.infrastructure.setEms);
}

-keep class com.netcetera.threeds.sdk.infrastructure.kv

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ky {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kx {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.kz {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lb {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.la {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.lo

-keep class com.netcetera.threeds.sdk.infrastructure.ll {
    com.netcetera.threeds.sdk.infrastructure.lo ThreeDS2Service(java.util.Map,com.netcetera.threeds.sdk.infrastructure.nk);
}

-keep class com.netcetera.threeds.sdk.infrastructure.lq {
    java.util.Map ThreeDS2ServiceInstance(java.util.Map);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lq {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lq {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lt {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ls {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lr {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lp {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lp {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lv {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lv {
    void ThreeDS2ServiceInstance();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ly {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lx {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lx {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lw {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lw {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mc {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mc {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ma {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ma {
    void getWarnings();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.mb {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.lz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.nk

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.no {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nt {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oa {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nx {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.nz {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oc$initialize {
    void initialize();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.od {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.og {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.og {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.of {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.of {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ok {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oh {
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.oj {
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keep class com.netcetera.threeds.sdk.infrastructure.oj$ThreeDS2Service

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oi {
    void ThreeDS2ServiceInstance();
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.op {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oo {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.oq {
    <init>(java.lang.Object);
    java.lang.String get(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.on {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.on {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ot {
    com.netcetera.threeds.sdk.infrastructure.ot ThreeDS2ServiceInstance(com.netcetera.threeds.sdk.api.configparameters.ConfigParameters);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ot {
    void ThreeDS2ServiceInstance();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ov {
    void initialize(java.lang.Object,java.lang.String);
    java.lang.String initialize(java.lang.String,java.lang.String);
    void ThreeDS2Service(int,int,java.lang.String);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ov {
    void init$0();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ou {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.os {
    <init>();
    void ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oy {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oy {
    void init$0();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oz {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.oz {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.ox

-keep class com.netcetera.threeds.sdk.infrastructure.pb

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pd {
    void initialize();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pe {
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pe {
    void get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pc {
    void getWarnings(java.lang.String);
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.Object[]);
    void get(java.lang.String);
    void initialize(java.lang.String,java.lang.Object[]);
    void get(java.lang.String,com.netcetera.threeds.sdk.infrastructure.ot);
}

-keep class com.netcetera.threeds.sdk.infrastructure.pi {
    java.lang.Object ThreeDS2Service(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pi {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pm {
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.pb,java.lang.String);
    java.lang.Object getWarnings(com.netcetera.threeds.sdk.infrastructure.ox,java.lang.String);
    void initialize(com.netcetera.threeds.sdk.infrastructure.pb,java.lang.String);
    java.lang.Object get(com.netcetera.threeds.sdk.infrastructure.ox,java.lang.String);
    java.util.Map ThreeDS2Service(com.netcetera.threeds.sdk.infrastructure.setProgressBackgroundTintList);
    void initialize();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pq {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pq {
    void init$0();
    void init$1();
    void init$2();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pr {
    java.lang.String[] initialize(java.lang.Object[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pr {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.py {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pz {
    java.lang.CharSequence initialize(java.lang.CharSequence,java.lang.CharSequence);
    boolean get(java.lang.CharSequence);
    boolean ThreeDS2ServiceInstance(java.lang.CharSequence);
    boolean get(java.lang.CharSequence,java.lang.CharSequence);
    boolean ThreeDS2Service(java.lang.CharSequence,java.lang.CharSequence);
    boolean initialize(java.lang.CharSequence);
    java.lang.String[] getWarnings(java.lang.String,java.lang.String);
    boolean getWarnings(java.lang.CharSequence[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pz {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.px {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.pw {
    java.lang.Long ThreeDS2Service;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.pw {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qc {
    java.util.Date get(java.util.Date,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qc {
    <init>();
}

-keep class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class org.bouncycastle.jce.provider.NcaBouncyCastleProvider {
    void getWarnings();
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qb {
    java.lang.String getWarnings(byte[]);
    byte[] ThreeDS2Service(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qb {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qe {
    <init>();
    byte[] getWarnings(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qd {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qa$get {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qg {
    <init>();
    com.netcetera.threeds.sdk.infrastructure.qg$ThreeDS2ServiceInstance ThreeDS2Service();
    com.netcetera.threeds.sdk.infrastructure.qg$ThreeDS2ServiceInstance getWarnings();
    java.security.SecureRandom get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qg$ThreeDS2ServiceInstance {
    void initialize(java.lang.String);
    java.lang.String ThreeDS2ServiceInstance();
    java.lang.String ThreeDS2Service();
    void getWarnings(java.lang.String);
    java.lang.String get();
    void ThreeDS2Service(java.lang.String);
    void get(java.lang.String);
    java.lang.String initialize();
    void ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String createTransaction();
    void cleanup(java.lang.String);
    java.lang.String getSDKVersion();
    void ThreeDS2ServiceInitializationCallback(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$4 {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qj$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qi {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qh {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qm {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qn {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qq {
    java.lang.String ThreeDS2ServiceInstance();
    boolean ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qr {
    boolean getWarnings(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.qr {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qt {
    com.netcetera.threeds.sdk.infrastructure.qt getWarnings;    <init>(com.netcetera.threeds.sdk.infrastructure.qt$ThreeDS2Service,java.lang.String[]);
    void initialize(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qt$ThreeDS2Service {
    com.netcetera.threeds.sdk.infrastructure.qt$ThreeDS2Service get;}

-keep class com.netcetera.threeds.sdk.infrastructure.qw {
    com.netcetera.threeds.sdk.infrastructure.qq get(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.qy {
    com.netcetera.threeds.sdk.infrastructure.qy ThreeDS2ServiceInstance();
    com.netcetera.threeds.sdk.infrastructure.qw getWarnings();
    com.netcetera.threeds.sdk.infrastructure.qw get();
    com.netcetera.threeds.sdk.infrastructure.qw ThreeDS2Service();
}

-keep class com.netcetera.threeds.sdk.infrastructure.qx {
    org.slf4j.Logger ThreeDS2ServiceInstance;    java.lang.String initialize;    java.lang.String get;    <init>();
    void get(java.lang.String);
    void ThreeDS2Service(java.lang.String);
    java.lang.String initialize();
    java.lang.String ThreeDS2ServiceInstance();
    void initialize(com.netcetera.threeds.sdk.infrastructure.st);
    void ThreeDS2ServiceInstance(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ru {
    com.netcetera.threeds.sdk.infrastructure.rw ThreeDS2Service(java.security.spec.ECParameterSpec);
    com.netcetera.threeds.sdk.infrastructure.rw ThreeDS2ServiceInstance(java.security.spec.ECParameterSpec,java.lang.String,java.security.SecureRandom);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ru {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rw {
    java.security.interfaces.ECPublicKey ThreeDS2ServiceInstance();
    java.security.interfaces.ECPrivateKey get();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rx {
    java.security.Key createTransaction();
    void getWarnings(java.lang.String);
    java.lang.String getSDKVersion();
}

-keep class com.netcetera.threeds.sdk.infrastructure.rx$get {
    com.netcetera.threeds.sdk.infrastructure.rx initialize(java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rx$get {
    <init>();
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.rz {
    void init$0();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sc {
    java.security.PrivateKey onCompleted();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sc$initialize {
    com.netcetera.threeds.sdk.infrastructure.sc initialize(java.security.Key);
    com.netcetera.threeds.sdk.infrastructure.sc ThreeDS2ServiceInstance(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.sc getWarnings(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sc$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sb$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sb$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sb$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sd$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sd$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sd$get {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sh {
    <init>();
    boolean get();
    java.lang.String getSDKVersion();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sf {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$initialize {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$get {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$ThreeDS2Service {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$getWarnings {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$ThreeDS2ServiceInstance {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.se$getSDKVersion {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sk {
    java.lang.String ThreeDS2Service(java.lang.String[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sk {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.si {
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
    void getWarnings(java.lang.String,java.lang.Object);
    void ThreeDS2ServiceInstance(java.lang.String,com.netcetera.threeds.sdk.infrastructure.rx);
    java.lang.String get(java.lang.String);
    java.lang.Long initialize(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.sc get(java.lang.String,java.lang.String);
    void ThreeDS2ServiceInstance(long,long);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.si {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sj {
    byte[] getWarnings;    <init>();
    void get(java.lang.String[]);
    void ThreeDS2ServiceInstance(java.lang.String);
    java.lang.String addParam();
    void ThreeDS2ServiceInstance(java.lang.String,java.lang.String);
    void getSDKVersion(java.lang.String);
    com.netcetera.threeds.sdk.infrastructure.si ConfigParameters();
    void get(java.lang.String,java.lang.String);
    java.lang.String getSDKInfo(java.lang.String);
    void cleanup(java.lang.String);
    java.lang.String onError();
    void createTransaction(java.lang.String);
    java.security.Key getParamValue();
    void ThreeDS2Service(java.security.Key);
    byte[] configureScheme();
    void ThreeDS2ServiceInstance(byte[]);
    boolean restrictedParameters();
    com.netcetera.threeds.sdk.infrastructure.qt removeParam();
    void get(com.netcetera.threeds.sdk.infrastructure.qt);
    void ConfigurationBuilder();
    com.netcetera.threeds.sdk.infrastructure.qg apiKey();
    void getWarnings(com.netcetera.threeds.sdk.infrastructure.qg);
}

-keep class com.netcetera.threeds.sdk.infrastructure.sl {
    void getWarnings(java.security.Key);
    java.security.Key initialize(java.security.Key,java.lang.Class);
    void get(byte[],java.lang.String);
    void initialize(java.security.Key,java.lang.String,int);
    java.lang.Object ThreeDS2ServiceInstance(java.lang.Object[],int,int,int);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sl {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sm {
    <init>(byte[]);
    java.lang.Object[] getWarnings(android.content.Context,int,int);
}

-keepclassmembers,allowshrinking,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sm {
    void ThreeDS2Service();
    void init$0();
    void init$1();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sr {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sq {
    <init>();
    boolean initialize();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sp {
    java.security.spec.ECParameterSpec ThreeDS2ServiceInstance;    java.lang.String get(java.security.spec.EllipticCurve);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sp {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sn {
    <init>(byte[]);
}

-keep class com.netcetera.threeds.sdk.infrastructure.st {
    com.netcetera.threeds.sdk.infrastructure.st initialize;    com.netcetera.threeds.sdk.infrastructure.st getWarnings;}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.ss {
    <init>();
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sw {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.su {
    byte[] get;    byte[] get(int);
    byte[] initialize(long);
    boolean get(byte[],byte[]);
    byte[] ThreeDS2ServiceInstance(byte[][]);
    byte[] ThreeDS2ServiceInstance(byte[],int,int);
    byte[] getWarnings(byte[]);
    byte[] ThreeDS2ServiceInstance(byte[]);
    int ThreeDS2Service(byte[]);
    int initialize(int);
    int getWarnings(int);
    byte[] initialize(int,java.security.SecureRandom);
    byte[] ThreeDS2ServiceInstance(int);
    java.lang.String get(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.su {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sv {
    java.lang.String ThreeDS2ServiceInstance(java.lang.Throwable);
    java.lang.String initialize(java.lang.Throwable,java.lang.Class);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sv {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sz {
    java.security.MessageDigest get(java.lang.String);
    java.security.MessageDigest ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.sz {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.sx {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.sy {
    <init>(java.lang.String);
}

-keep class com.netcetera.threeds.sdk.infrastructure.ta {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.tb {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.tf {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.te {
    java.lang.String getWarnings(byte[],java.lang.String);
    byte[] initialize(java.lang.String);
    byte[] ThreeDS2ServiceInstance(java.lang.String);
    byte[] ThreeDS2Service(java.lang.String,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.te {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.tg {
    <init>(java.lang.String);
    <init>(java.lang.String,java.lang.Throwable);
}

-keep class com.netcetera.threeds.sdk.infrastructure.tc {
    javax.crypto.Mac getWarnings(java.lang.String,java.security.Key,java.lang.String);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.tc {
    <init>();
}

-keep class com.netcetera.threeds.sdk.infrastructure.td {
    byte[] initialize(byte[]);
    byte[] ThreeDS2ServiceInstance(byte[]);
}

-keepclassmembers,allowoptimization,allowobfuscation class com.netcetera.threeds.sdk.infrastructure.tj {
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
