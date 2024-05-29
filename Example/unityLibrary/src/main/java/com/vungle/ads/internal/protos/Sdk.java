//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.vungle.ads.internal.protos;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public final class Sdk {
    private Sdk() {
    }

    public static void registerAllExtensions(ExtensionRegistryLite registry) {
    }

    public static final class SDKMetric extends GeneratedMessageLite<SDKMetric, SDKMetric.Builder> implements SDKMetricOrBuilder {
        public static final int TYPE_FIELD_NUMBER = 1;
        private int type_;
        public static final int VALUE_FIELD_NUMBER = 2;
        private long value_;
        public static final int META_FIELD_NUMBER = 3;
        private String meta_ = "";
        public static final int MAKE_FIELD_NUMBER = 4;
        private String make_ = "";
        public static final int MODEL_FIELD_NUMBER = 5;
        private String model_ = "";
        public static final int OS_FIELD_NUMBER = 6;
        private String os_ = "";
        public static final int OSVERSION_FIELD_NUMBER = 7;
        private String osVersion_ = "";
        public static final int CONNECTIONTYPE_FIELD_NUMBER = 8;
        private String connectionType_ = "";
        public static final int CONNECTIONTYPEDETAIL_FIELD_NUMBER = 9;
        private String connectionTypeDetail_ = "";
        public static final int PLACEMENTREFERENCEID_FIELD_NUMBER = 10;
        private String placementReferenceId_ = "";
        public static final int CREATIVEID_FIELD_NUMBER = 11;
        private String creativeId_ = "";
        public static final int EVENTID_FIELD_NUMBER = 12;
        private String eventId_ = "";
        private static final SDKMetric DEFAULT_INSTANCE;
        private static volatile Parser<SDKMetric> PARSER;

        private SDKMetric() {
        }

        public int getTypeValue() {
            return this.type_;
        }

        public SDKMetricType getType() {
            SDKMetricType result = Sdk.SDKMetric.SDKMetricType.forNumber(this.type_);
            return result == null ? Sdk.SDKMetric.SDKMetricType.UNRECOGNIZED : result;
        }

        private void setTypeValue(int value) {
            this.type_ = value;
        }

        private void setType(SDKMetricType value) {
            this.type_ = value.getNumber();
        }

        private void clearType() {
            this.type_ = 0;
        }

        public long getValue() {
            return this.value_;
        }

        private void setValue(long value) {
            this.value_ = value;
        }

        private void clearValue() {
            this.value_ = 0L;
        }

        public String getMeta() {
            return this.meta_;
        }

        public ByteString getMetaBytes() {
            return ByteString.copyFromUtf8(this.meta_);
        }

        private void setMeta(String value) {
            Class<?> valueClass = value.getClass();
            this.meta_ = value;
        }

        private void clearMeta() {
            this.meta_ = getDefaultInstance().getMeta();
        }

        private void setMetaBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.meta_ = value.toStringUtf8();
        }

        public String getMake() {
            return this.make_;
        }

        public ByteString getMakeBytes() {
            return ByteString.copyFromUtf8(this.make_);
        }

        private void setMake(String value) {
            Class<?> valueClass = value.getClass();
            this.make_ = value;
        }

        private void clearMake() {
            this.make_ = getDefaultInstance().getMake();
        }

        private void setMakeBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.make_ = value.toStringUtf8();
        }

        public String getModel() {
            return this.model_;
        }

        public ByteString getModelBytes() {
            return ByteString.copyFromUtf8(this.model_);
        }

        private void setModel(String value) {
            Class<?> valueClass = value.getClass();
            this.model_ = value;
        }

        private void clearModel() {
            this.model_ = getDefaultInstance().getModel();
        }

        private void setModelBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.model_ = value.toStringUtf8();
        }

        public String getOs() {
            return this.os_;
        }

        public ByteString getOsBytes() {
            return ByteString.copyFromUtf8(this.os_);
        }

        private void setOs(String value) {
            Class<?> valueClass = value.getClass();
            this.os_ = value;
        }

        private void clearOs() {
            this.os_ = getDefaultInstance().getOs();
        }

        private void setOsBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.os_ = value.toStringUtf8();
        }

        public String getOsVersion() {
            return this.osVersion_;
        }

        public ByteString getOsVersionBytes() {
            return ByteString.copyFromUtf8(this.osVersion_);
        }

        private void setOsVersion(String value) {
            Class<?> valueClass = value.getClass();
            this.osVersion_ = value;
        }

        private void clearOsVersion() {
            this.osVersion_ = getDefaultInstance().getOsVersion();
        }

        private void setOsVersionBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.osVersion_ = value.toStringUtf8();
        }

        public String getConnectionType() {
            return this.connectionType_;
        }

        public ByteString getConnectionTypeBytes() {
            return ByteString.copyFromUtf8(this.connectionType_);
        }

        private void setConnectionType(String value) {
            Class<?> valueClass = value.getClass();
            this.connectionType_ = value;
        }

        private void clearConnectionType() {
            this.connectionType_ = getDefaultInstance().getConnectionType();
        }

        private void setConnectionTypeBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.connectionType_ = value.toStringUtf8();
        }

        public String getConnectionTypeDetail() {
            return this.connectionTypeDetail_;
        }

        public ByteString getConnectionTypeDetailBytes() {
            return ByteString.copyFromUtf8(this.connectionTypeDetail_);
        }

        private void setConnectionTypeDetail(String value) {
            Class<?> valueClass = value.getClass();
            this.connectionTypeDetail_ = value;
        }

        private void clearConnectionTypeDetail() {
            this.connectionTypeDetail_ = getDefaultInstance().getConnectionTypeDetail();
        }

        private void setConnectionTypeDetailBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.connectionTypeDetail_ = value.toStringUtf8();
        }

        public String getPlacementReferenceId() {
            return this.placementReferenceId_;
        }

        public ByteString getPlacementReferenceIdBytes() {
            return ByteString.copyFromUtf8(this.placementReferenceId_);
        }

        private void setPlacementReferenceId(String value) {
            Class<?> valueClass = value.getClass();
            this.placementReferenceId_ = value;
        }

        private void clearPlacementReferenceId() {
            this.placementReferenceId_ = getDefaultInstance().getPlacementReferenceId();
        }

        private void setPlacementReferenceIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.placementReferenceId_ = value.toStringUtf8();
        }

        public String getCreativeId() {
            return this.creativeId_;
        }

        public ByteString getCreativeIdBytes() {
            return ByteString.copyFromUtf8(this.creativeId_);
        }

        private void setCreativeId(String value) {
            Class<?> valueClass = value.getClass();
            this.creativeId_ = value;
        }

        private void clearCreativeId() {
            this.creativeId_ = getDefaultInstance().getCreativeId();
        }

        private void setCreativeIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.creativeId_ = value.toStringUtf8();
        }

        public String getEventId() {
            return this.eventId_;
        }

        public ByteString getEventIdBytes() {
            return ByteString.copyFromUtf8(this.eventId_);
        }

        private void setEventId(String value) {
            Class<?> valueClass = value.getClass();
            this.eventId_ = value;
        }

        private void clearEventId() {
            this.eventId_ = getDefaultInstance().getEventId();
        }

        private void setEventIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.eventId_ = value.toStringUtf8();
        }

        public static SDKMetric parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKMetric parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKMetric parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKMetric parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKMetric parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKMetric parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKMetric parseFrom(InputStream input) throws IOException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKMetric parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKMetric parseDelimitedFrom(InputStream input) throws IOException {
            return (SDKMetric)parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKMetric parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKMetric)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKMetric parseFrom(CodedInputStream input) throws IOException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKMetric parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKMetric)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(SDKMetric prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE:
                    return new SDKMetric();
                case NEW_BUILDER:
                    return new Builder();
                case BUILD_MESSAGE_INFO:
                    Object[] objects = new Object[]{"type_", "value_", "meta_", "make_", "model_", "os_", "osVersion_", "connectionType_", "connectionTypeDetail_", "placementReferenceId_", "creativeId_", "eventId_"};
                    String info = "\u0000\f\u0000\u0000\u0001\f\f\u0000\u0000\u0000\u0001\f\u0002\u0002\u0003Ȉ\u0004Ȉ\u0005Ȉ\u0006Ȉ\u0007Ȉ\bȈ\tȈ\nȈ\u000bȈ\fȈ";
                    return newMessageInfo(DEFAULT_INSTANCE, info, objects);
                case GET_DEFAULT_INSTANCE:
                    return DEFAULT_INSTANCE;
                case GET_PARSER:
                    Parser<SDKMetric> parser = PARSER;
                    if (parser == null) {
                        Class var5 = SDKMetric.class;
                        synchronized(SDKMetric.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = (Parser)parser;
                            }
                        }
                    }

                    return parser;
                case GET_MEMOIZED_IS_INITIALIZED:
                    return 1;
                case SET_MEMOIZED_IS_INITIALIZED:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static SDKMetric getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SDKMetric> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            SDKMetric defaultInstance = new SDKMetric();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(SDKMetric.class, defaultInstance);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SDKMetric, Builder> implements SDKMetricOrBuilder {
            private Builder() {
                super(Sdk.SDKMetric.DEFAULT_INSTANCE);
            }

            public int getTypeValue() {
                return ((SDKMetric)this.instance).getTypeValue();
            }

            public Builder setTypeValue(int value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setTypeValue(value);
                return this;
            }

            public SDKMetricType getType() {
                return ((SDKMetric)this.instance).getType();
            }

            public Builder setType(SDKMetricType value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setType(value);
                return this;
            }

            public Builder clearType() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearType();
                return this;
            }

            public long getValue() {
                return ((SDKMetric)this.instance).getValue();
            }

            public Builder setValue(long value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setValue(value);
                return this;
            }

            public Builder clearValue() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearValue();
                return this;
            }

            public String getMeta() {
                return ((SDKMetric)this.instance).getMeta();
            }

            public ByteString getMetaBytes() {
                return ((SDKMetric)this.instance).getMetaBytes();
            }

            public Builder setMeta(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setMeta(value);
                return this;
            }

            public Builder clearMeta() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearMeta();
                return this;
            }

            public Builder setMetaBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setMetaBytes(value);
                return this;
            }

            public String getMake() {
                return ((SDKMetric)this.instance).getMake();
            }

            public ByteString getMakeBytes() {
                return ((SDKMetric)this.instance).getMakeBytes();
            }

            public Builder setMake(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setMake(value);
                return this;
            }

            public Builder clearMake() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearMake();
                return this;
            }

            public Builder setMakeBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setMakeBytes(value);
                return this;
            }

            public String getModel() {
                return ((SDKMetric)this.instance).getModel();
            }

            public ByteString getModelBytes() {
                return ((SDKMetric)this.instance).getModelBytes();
            }

            public Builder setModel(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setModel(value);
                return this;
            }

            public Builder clearModel() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearModel();
                return this;
            }

            public Builder setModelBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setModelBytes(value);
                return this;
            }

            public String getOs() {
                return ((SDKMetric)this.instance).getOs();
            }

            public ByteString getOsBytes() {
                return ((SDKMetric)this.instance).getOsBytes();
            }

            public Builder setOs(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setOs(value);
                return this;
            }

            public Builder clearOs() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearOs();
                return this;
            }

            public Builder setOsBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setOsBytes(value);
                return this;
            }

            public String getOsVersion() {
                return ((SDKMetric)this.instance).getOsVersion();
            }

            public ByteString getOsVersionBytes() {
                return ((SDKMetric)this.instance).getOsVersionBytes();
            }

            public Builder setOsVersion(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setOsVersion(value);
                return this;
            }

            public Builder clearOsVersion() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearOsVersion();
                return this;
            }

            public Builder setOsVersionBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setOsVersionBytes(value);
                return this;
            }

            public String getConnectionType() {
                return ((SDKMetric)this.instance).getConnectionType();
            }

            public ByteString getConnectionTypeBytes() {
                return ((SDKMetric)this.instance).getConnectionTypeBytes();
            }

            public Builder setConnectionType(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setConnectionType(value);
                return this;
            }

            public Builder clearConnectionType() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearConnectionType();
                return this;
            }

            public Builder setConnectionTypeBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setConnectionTypeBytes(value);
                return this;
            }

            public String getConnectionTypeDetail() {
                return ((SDKMetric)this.instance).getConnectionTypeDetail();
            }

            public ByteString getConnectionTypeDetailBytes() {
                return ((SDKMetric)this.instance).getConnectionTypeDetailBytes();
            }

            public Builder setConnectionTypeDetail(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setConnectionTypeDetail(value);
                return this;
            }

            public Builder clearConnectionTypeDetail() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearConnectionTypeDetail();
                return this;
            }

            public Builder setConnectionTypeDetailBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setConnectionTypeDetailBytes(value);
                return this;
            }

            public String getPlacementReferenceId() {
                return ((SDKMetric)this.instance).getPlacementReferenceId();
            }

            public ByteString getPlacementReferenceIdBytes() {
                return ((SDKMetric)this.instance).getPlacementReferenceIdBytes();
            }

            public Builder setPlacementReferenceId(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setPlacementReferenceId(value);
                return this;
            }

            public Builder clearPlacementReferenceId() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearPlacementReferenceId();
                return this;
            }

            public Builder setPlacementReferenceIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setPlacementReferenceIdBytes(value);
                return this;
            }

            public String getCreativeId() {
                return ((SDKMetric)this.instance).getCreativeId();
            }

            public ByteString getCreativeIdBytes() {
                return ((SDKMetric)this.instance).getCreativeIdBytes();
            }

            public Builder setCreativeId(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setCreativeId(value);
                return this;
            }

            public Builder clearCreativeId() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearCreativeId();
                return this;
            }

            public Builder setCreativeIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setCreativeIdBytes(value);
                return this;
            }

            public String getEventId() {
                return ((SDKMetric)this.instance).getEventId();
            }

            public ByteString getEventIdBytes() {
                return ((SDKMetric)this.instance).getEventIdBytes();
            }

            public Builder setEventId(String value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setEventId(value);
                return this;
            }

            public Builder clearEventId() {
                this.copyOnWrite();
                ((SDKMetric)this.instance).clearEventId();
                return this;
            }

            public Builder setEventIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKMetric)this.instance).setEventIdBytes(value);
                return this;
            }
        }

        public static enum SDKMetricType implements Internal.EnumLite {
            UNKNOWN_METRIC_TYPE(0),
            AD_REQUEST_TO_RESPONSE_DURATION_MS(1),
            AD_RESPONSE_TO_SHOW_DURATION_MS(2),
            AD_SHOW_TO_DISPLAY_DURATION_MS(3),
            AD_DISPLAY_TO_CLICK_DURATION_MS(4),
            IOS_STORE_KIT_LOAD_TIME_MS(5),
            INIT_REQUEST_TO_RESPONSE_DURATION_MS(6),
            ASSET_DOWNLOAD_DURATION_MS(7),
            LOCAL_ASSETS_USED(8),
            REMOTE_ASSETS_USED(9),
            TEMPLATE_DOWNLOAD_DURATION_MS(10),
            AD_REQUEST_TO_CALLBACK_DURATION_MS(11),
            AD_REQUEST_TO_CALLBACK_ADO_DURATION_MS(12),
            ASSET_FILE_SIZE(13),
            USER_AGENT_LOAD_DURATION_MS(14),
            TEMPLATE_ZIP_SIZE(15),
            CACHED_ASSETS_USED(16),
            LOAD_AD_API(17),
            TPAT_FIRED(18),
            TPAT_SUCCESS(19),
            WIN_NOTIF_FIRED(20),
            WIN_NOTIF_SUCCESS(21),
            /** @deprecated */
            @Deprecated
            AD_EXPIRED_BEFORE_PLAY(22),
            PLAY_AD_API(23),
            AD_LOAD_FAIL(24),
            VIEW_NOT_VISIBLE_ON_PLAY(25),
            MRAID_DOWNLOAD_JS_RETRY_SUCCESS(26),
            OMSDK_DOWNLOAD_JS_RETRY_SUCCESS(27),
            PRIVACY_URL_OPENED(28),
            NOTIFICATION_REDIRECT(29),
            AD_PLAY_RESET_ON_DEINIT(30),
            SKOVERLAY_PRESENTED_FOR_AD(2000),
            SAFARI_PRESENTED_FOR_AD(2001),
            STORE_KIT_PRESENTED_FOR_AD(2002),
            STORE_KIT_NOT_READY(2003),
            LAUNCH_STORE_KIT_REQUEST(2004),
            LAUNCH_SKOVERLAY_REQUEST(2005),
            LAUNCH_SAFARI_REQUEST(2006),
            IDFV_RESTRICTED(2007),
            NOTIFICATION_WAIT_FOR_CONNECTIVITY(2008),
            IDFV_VALUE_CHANGED(2009),
            UNRECOGNIZED(-1);

            public static final int UNKNOWN_METRIC_TYPE_VALUE = 0;
            public static final int AD_REQUEST_TO_RESPONSE_DURATION_MS_VALUE = 1;
            public static final int AD_RESPONSE_TO_SHOW_DURATION_MS_VALUE = 2;
            public static final int AD_SHOW_TO_DISPLAY_DURATION_MS_VALUE = 3;
            public static final int AD_DISPLAY_TO_CLICK_DURATION_MS_VALUE = 4;
            public static final int IOS_STORE_KIT_LOAD_TIME_MS_VALUE = 5;
            public static final int INIT_REQUEST_TO_RESPONSE_DURATION_MS_VALUE = 6;
            public static final int ASSET_DOWNLOAD_DURATION_MS_VALUE = 7;
            public static final int LOCAL_ASSETS_USED_VALUE = 8;
            public static final int REMOTE_ASSETS_USED_VALUE = 9;
            public static final int TEMPLATE_DOWNLOAD_DURATION_MS_VALUE = 10;
            public static final int AD_REQUEST_TO_CALLBACK_DURATION_MS_VALUE = 11;
            public static final int AD_REQUEST_TO_CALLBACK_ADO_DURATION_MS_VALUE = 12;
            public static final int ASSET_FILE_SIZE_VALUE = 13;
            public static final int USER_AGENT_LOAD_DURATION_MS_VALUE = 14;
            public static final int TEMPLATE_ZIP_SIZE_VALUE = 15;
            public static final int CACHED_ASSETS_USED_VALUE = 16;
            public static final int LOAD_AD_API_VALUE = 17;
            public static final int TPAT_FIRED_VALUE = 18;
            public static final int TPAT_SUCCESS_VALUE = 19;
            public static final int WIN_NOTIF_FIRED_VALUE = 20;
            public static final int WIN_NOTIF_SUCCESS_VALUE = 21;
            /** @deprecated */
            @Deprecated
            public static final int AD_EXPIRED_BEFORE_PLAY_VALUE = 22;
            public static final int PLAY_AD_API_VALUE = 23;
            public static final int AD_LOAD_FAIL_VALUE = 24;
            public static final int VIEW_NOT_VISIBLE_ON_PLAY_VALUE = 25;
            public static final int MRAID_DOWNLOAD_JS_RETRY_SUCCESS_VALUE = 26;
            public static final int OMSDK_DOWNLOAD_JS_RETRY_SUCCESS_VALUE = 27;
            public static final int PRIVACY_URL_OPENED_VALUE = 28;
            public static final int NOTIFICATION_REDIRECT_VALUE = 29;
            public static final int AD_PLAY_RESET_ON_DEINIT_VALUE = 30;
            public static final int SKOVERLAY_PRESENTED_FOR_AD_VALUE = 2000;
            public static final int SAFARI_PRESENTED_FOR_AD_VALUE = 2001;
            public static final int STORE_KIT_PRESENTED_FOR_AD_VALUE = 2002;
            public static final int STORE_KIT_NOT_READY_VALUE = 2003;
            public static final int LAUNCH_STORE_KIT_REQUEST_VALUE = 2004;
            public static final int LAUNCH_SKOVERLAY_REQUEST_VALUE = 2005;
            public static final int LAUNCH_SAFARI_REQUEST_VALUE = 2006;
            public static final int IDFV_RESTRICTED_VALUE = 2007;
            public static final int NOTIFICATION_WAIT_FOR_CONNECTIVITY_VALUE = 2008;
            public static final int IDFV_VALUE_CHANGED_VALUE = 2009;
            private static final Internal.EnumLiteMap<SDKMetricType> internalValueMap = new Internal.EnumLiteMap<SDKMetricType>() {
                public SDKMetricType findValueByNumber(int number) {
                    return Sdk.SDKMetric.SDKMetricType.forNumber(number);
                }
            };
            private final int value;

            public final int getNumber() {
                if (this == UNRECOGNIZED) {
                    throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
                } else {
                    return this.value;
                }
            }

            /** @deprecated */
            @Deprecated
            public static SDKMetricType valueOf(int value) {
                return forNumber(value);
            }

            public static SDKMetricType forNumber(int value) {
                switch (value) {
                    case 0:
                        return UNKNOWN_METRIC_TYPE;
                    case 1:
                        return AD_REQUEST_TO_RESPONSE_DURATION_MS;
                    case 2:
                        return AD_RESPONSE_TO_SHOW_DURATION_MS;
                    case 3:
                        return AD_SHOW_TO_DISPLAY_DURATION_MS;
                    case 4:
                        return AD_DISPLAY_TO_CLICK_DURATION_MS;
                    case 5:
                        return IOS_STORE_KIT_LOAD_TIME_MS;
                    case 6:
                        return INIT_REQUEST_TO_RESPONSE_DURATION_MS;
                    case 7:
                        return ASSET_DOWNLOAD_DURATION_MS;
                    case 8:
                        return LOCAL_ASSETS_USED;
                    case 9:
                        return REMOTE_ASSETS_USED;
                    case 10:
                        return TEMPLATE_DOWNLOAD_DURATION_MS;
                    case 11:
                        return AD_REQUEST_TO_CALLBACK_DURATION_MS;
                    case 12:
                        return AD_REQUEST_TO_CALLBACK_ADO_DURATION_MS;
                    case 13:
                        return ASSET_FILE_SIZE;
                    case 14:
                        return USER_AGENT_LOAD_DURATION_MS;
                    case 15:
                        return TEMPLATE_ZIP_SIZE;
                    case 16:
                        return CACHED_ASSETS_USED;
                    case 17:
                        return LOAD_AD_API;
                    case 18:
                        return TPAT_FIRED;
                    case 19:
                        return TPAT_SUCCESS;
                    case 20:
                        return WIN_NOTIF_FIRED;
                    case 21:
                        return WIN_NOTIF_SUCCESS;
                    case 22:
                        return AD_EXPIRED_BEFORE_PLAY;
                    case 23:
                        return PLAY_AD_API;
                    case 24:
                        return AD_LOAD_FAIL;
                    case 25:
                        return VIEW_NOT_VISIBLE_ON_PLAY;
                    case 26:
                        return MRAID_DOWNLOAD_JS_RETRY_SUCCESS;
                    case 27:
                        return OMSDK_DOWNLOAD_JS_RETRY_SUCCESS;
                    case 28:
                        return PRIVACY_URL_OPENED;
                    case 29:
                        return NOTIFICATION_REDIRECT;
                    case 30:
                        return AD_PLAY_RESET_ON_DEINIT;
                    case 2000:
                        return SKOVERLAY_PRESENTED_FOR_AD;
                    case 2001:
                        return SAFARI_PRESENTED_FOR_AD;
                    case 2002:
                        return STORE_KIT_PRESENTED_FOR_AD;
                    case 2003:
                        return STORE_KIT_NOT_READY;
                    case 2004:
                        return LAUNCH_STORE_KIT_REQUEST;
                    case 2005:
                        return LAUNCH_SKOVERLAY_REQUEST;
                    case 2006:
                        return LAUNCH_SAFARI_REQUEST;
                    case 2007:
                        return IDFV_RESTRICTED;
                    case 2008:
                        return NOTIFICATION_WAIT_FOR_CONNECTIVITY;
                    case 2009:
                        return IDFV_VALUE_CHANGED;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<SDKMetricType> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return Sdk.SDKMetric.SDKMetricType.SDKMetricTypeVerifier.INSTANCE;
            }

            private SDKMetricType(int value) {
                this.value = value;
            }

            private static final class SDKMetricTypeVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new SDKMetricTypeVerifier();

                private SDKMetricTypeVerifier() {
                }

                public boolean isInRange(int number) {
                    return Sdk.SDKMetric.SDKMetricType.forNumber(number) != null;
                }
            }
        }
    }

    public interface SDKMetricOrBuilder extends MessageLiteOrBuilder {
        int getTypeValue();

        SDKMetric.SDKMetricType getType();

        long getValue();

        String getMeta();

        ByteString getMetaBytes();

        String getMake();

        ByteString getMakeBytes();

        String getModel();

        ByteString getModelBytes();

        String getOs();

        ByteString getOsBytes();

        String getOsVersion();

        ByteString getOsVersionBytes();

        String getConnectionType();

        ByteString getConnectionTypeBytes();

        String getConnectionTypeDetail();

        ByteString getConnectionTypeDetailBytes();

        String getPlacementReferenceId();

        ByteString getPlacementReferenceIdBytes();

        String getCreativeId();

        ByteString getCreativeIdBytes();

        String getEventId();

        ByteString getEventIdBytes();
    }

    public static final class MetricBatch extends GeneratedMessageLite<MetricBatch, MetricBatch.Builder> implements MetricBatchOrBuilder {
        public static final int METRICS_FIELD_NUMBER = 1;
        private Internal.ProtobufList<SDKMetric> metrics_ = emptyProtobufList();
        private static final MetricBatch DEFAULT_INSTANCE;
        private static volatile Parser<MetricBatch> PARSER;

        private MetricBatch() {
        }

        public List<SDKMetric> getMetricsList() {
            return this.metrics_;
        }

        public List<? extends SDKMetricOrBuilder> getMetricsOrBuilderList() {
            return this.metrics_;
        }

        public int getMetricsCount() {
            return this.metrics_.size();
        }

        public SDKMetric getMetrics(int index) {
            return (SDKMetric)this.metrics_.get(index);
        }

        public SDKMetricOrBuilder getMetricsOrBuilder(int index) {
            return (SDKMetricOrBuilder)this.metrics_.get(index);
        }

        private void ensureMetricsIsMutable() {
            Internal.ProtobufList<SDKMetric> tmp = this.metrics_;
            if (!tmp.isModifiable()) {
                this.metrics_ = GeneratedMessageLite.mutableCopy(tmp);
            }

        }

        private void setMetrics(int index, SDKMetric value) {
            value.getClass();
            this.ensureMetricsIsMutable();
            this.metrics_.set(index, value);
        }

        private void addMetrics(SDKMetric value) {
            value.getClass();
            this.ensureMetricsIsMutable();
            this.metrics_.add(value);
        }

        private void addMetrics(int index, SDKMetric value) {
            value.getClass();
            this.ensureMetricsIsMutable();
            this.metrics_.add(index, value);
        }

        private void addAllMetrics(Iterable<? extends SDKMetric> values) {
            this.ensureMetricsIsMutable();
            AbstractMessageLite.addAll(values, this.metrics_);
        }

        private void clearMetrics() {
            this.metrics_ = emptyProtobufList();
        }

        private void removeMetrics(int index) {
            this.ensureMetricsIsMutable();
            this.metrics_.remove(index);
        }

        public static MetricBatch parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static MetricBatch parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static MetricBatch parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static MetricBatch parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static MetricBatch parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static MetricBatch parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static MetricBatch parseFrom(InputStream input) throws IOException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static MetricBatch parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static MetricBatch parseDelimitedFrom(InputStream input) throws IOException {
            return (MetricBatch)parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static MetricBatch parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MetricBatch)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static MetricBatch parseFrom(CodedInputStream input) throws IOException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static MetricBatch parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (MetricBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(MetricBatch prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE:
                    return new MetricBatch();
                case NEW_BUILDER:
                    return new Builder();
                case BUILD_MESSAGE_INFO:
                    Object[] objects = new Object[]{"metrics_", SDKMetric.class};
                    String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b";
                    return newMessageInfo(DEFAULT_INSTANCE, info, objects);
                case GET_DEFAULT_INSTANCE:
                    return DEFAULT_INSTANCE;
                case GET_PARSER:
                    Parser<MetricBatch> parser = PARSER;
                    if (parser == null) {
                        Class var5 = MetricBatch.class;
                        synchronized(MetricBatch.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = (Parser)parser;
                            }
                        }
                    }

                    return parser;
                case GET_MEMOIZED_IS_INITIALIZED:
                    return 1;
                case SET_MEMOIZED_IS_INITIALIZED:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static MetricBatch getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<MetricBatch> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            MetricBatch defaultInstance = new MetricBatch();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(MetricBatch.class, defaultInstance);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<MetricBatch, Builder> implements MetricBatchOrBuilder {
            private Builder() {
                super(Sdk.MetricBatch.DEFAULT_INSTANCE);
            }

            public List<SDKMetric> getMetricsList() {
                return Collections.unmodifiableList(((MetricBatch)this.instance).getMetricsList());
            }

            public int getMetricsCount() {
                return ((MetricBatch)this.instance).getMetricsCount();
            }

            public SDKMetric getMetrics(int index) {
                return ((MetricBatch)this.instance).getMetrics(index);
            }

            public Builder setMetrics(int index, SDKMetric value) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).setMetrics(index, value);
                return this;
            }

            public Builder setMetrics(int index, SDKMetric.Builder builderForValue) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).setMetrics(index, (SDKMetric)builderForValue.build());
                return this;
            }

            public Builder addMetrics(SDKMetric value) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).addMetrics(value);
                return this;
            }

            public Builder addMetrics(int index, SDKMetric value) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).addMetrics(index, value);
                return this;
            }

            public Builder addMetrics(SDKMetric.Builder builderForValue) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).addMetrics((SDKMetric)builderForValue.build());
                return this;
            }

            public Builder addMetrics(int index, SDKMetric.Builder builderForValue) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).addMetrics(index, (SDKMetric)builderForValue.build());
                return this;
            }

            public Builder addAllMetrics(Iterable<? extends SDKMetric> values) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).addAllMetrics(values);
                return this;
            }

            public Builder clearMetrics() {
                this.copyOnWrite();
                ((MetricBatch)this.instance).clearMetrics();
                return this;
            }

            public Builder removeMetrics(int index) {
                this.copyOnWrite();
                ((MetricBatch)this.instance).removeMetrics(index);
                return this;
            }
        }
    }

    public interface MetricBatchOrBuilder extends MessageLiteOrBuilder {
        List<SDKMetric> getMetricsList();

        SDKMetric getMetrics(int var1);

        int getMetricsCount();
    }

    public static final class SDKError extends GeneratedMessageLite<SDKError, SDKError.Builder> implements SDKErrorOrBuilder {
        public static final int AT_FIELD_NUMBER = 1;
        private long at_;
        public static final int REASON_FIELD_NUMBER = 2;
        private int reason_;
        public static final int MESSAGE_FIELD_NUMBER = 3;
        private String message_ = "";
        public static final int EVENTID_FIELD_NUMBER = 4;
        private String eventId_ = "";
        public static final int MAKE_FIELD_NUMBER = 5;
        private String make_ = "";
        public static final int MODEL_FIELD_NUMBER = 6;
        private String model_ = "";
        public static final int OS_FIELD_NUMBER = 7;
        private String os_ = "";
        public static final int OSVERSION_FIELD_NUMBER = 8;
        private String osVersion_ = "";
        public static final int CONNECTIONTYPE_FIELD_NUMBER = 9;
        private String connectionType_ = "";
        public static final int CONNECTIONTYPEDETAIL_FIELD_NUMBER = 10;
        private String connectionTypeDetail_ = "";
        public static final int PLACEMENTREFERENCEID_FIELD_NUMBER = 11;
        private String placementReferenceId_ = "";
        public static final int CREATIVEID_FIELD_NUMBER = 12;
        private String creativeId_ = "";
        public static final int CONNECTIONTYPEDETAILANDROID_FIELD_NUMBER = 101;
        private String connectionTypeDetailAndroid_ = "";
        private static final SDKError DEFAULT_INSTANCE;
        private static volatile Parser<SDKError> PARSER;

        private SDKError() {
        }

        public long getAt() {
            return this.at_;
        }

        private void setAt(long value) {
            this.at_ = value;
        }

        private void clearAt() {
            this.at_ = 0L;
        }

        public int getReasonValue() {
            return this.reason_;
        }

        public Reason getReason() {
            Reason result = Sdk.SDKError.Reason.forNumber(this.reason_);
            return result == null ? Sdk.SDKError.Reason.UNRECOGNIZED : result;
        }

        private void setReasonValue(int value) {
            this.reason_ = value;
        }

        private void setReason(Reason value) {
            this.reason_ = value.getNumber();
        }

        private void clearReason() {
            this.reason_ = 0;
        }

        public String getMessage() {
            return this.message_;
        }

        public ByteString getMessageBytes() {
            return ByteString.copyFromUtf8(this.message_);
        }

        private void setMessage(String value) {
            Class<?> valueClass = value.getClass();
            this.message_ = value;
        }

        private void clearMessage() {
            this.message_ = getDefaultInstance().getMessage();
        }

        private void setMessageBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.message_ = value.toStringUtf8();
        }

        public String getEventId() {
            return this.eventId_;
        }

        public ByteString getEventIdBytes() {
            return ByteString.copyFromUtf8(this.eventId_);
        }

        private void setEventId(String value) {
            Class<?> valueClass = value.getClass();
            this.eventId_ = value;
        }

        private void clearEventId() {
            this.eventId_ = getDefaultInstance().getEventId();
        }

        private void setEventIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.eventId_ = value.toStringUtf8();
        }

        public String getMake() {
            return this.make_;
        }

        public ByteString getMakeBytes() {
            return ByteString.copyFromUtf8(this.make_);
        }

        private void setMake(String value) {
            Class<?> valueClass = value.getClass();
            this.make_ = value;
        }

        private void clearMake() {
            this.make_ = getDefaultInstance().getMake();
        }

        private void setMakeBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.make_ = value.toStringUtf8();
        }

        public String getModel() {
            return this.model_;
        }

        public ByteString getModelBytes() {
            return ByteString.copyFromUtf8(this.model_);
        }

        private void setModel(String value) {
            Class<?> valueClass = value.getClass();
            this.model_ = value;
        }

        private void clearModel() {
            this.model_ = getDefaultInstance().getModel();
        }

        private void setModelBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.model_ = value.toStringUtf8();
        }

        public String getOs() {
            return this.os_;
        }

        public ByteString getOsBytes() {
            return ByteString.copyFromUtf8(this.os_);
        }

        private void setOs(String value) {
            Class<?> valueClass = value.getClass();
            this.os_ = value;
        }

        private void clearOs() {
            this.os_ = getDefaultInstance().getOs();
        }

        private void setOsBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.os_ = value.toStringUtf8();
        }

        public String getOsVersion() {
            return this.osVersion_;
        }

        public ByteString getOsVersionBytes() {
            return ByteString.copyFromUtf8(this.osVersion_);
        }

        private void setOsVersion(String value) {
            Class<?> valueClass = value.getClass();
            this.osVersion_ = value;
        }

        private void clearOsVersion() {
            this.osVersion_ = getDefaultInstance().getOsVersion();
        }

        private void setOsVersionBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.osVersion_ = value.toStringUtf8();
        }

        public String getConnectionType() {
            return this.connectionType_;
        }

        public ByteString getConnectionTypeBytes() {
            return ByteString.copyFromUtf8(this.connectionType_);
        }

        private void setConnectionType(String value) {
            Class<?> valueClass = value.getClass();
            this.connectionType_ = value;
        }

        private void clearConnectionType() {
            this.connectionType_ = getDefaultInstance().getConnectionType();
        }

        private void setConnectionTypeBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.connectionType_ = value.toStringUtf8();
        }

        public String getConnectionTypeDetail() {
            return this.connectionTypeDetail_;
        }

        public ByteString getConnectionTypeDetailBytes() {
            return ByteString.copyFromUtf8(this.connectionTypeDetail_);
        }

        private void setConnectionTypeDetail(String value) {
            Class<?> valueClass = value.getClass();
            this.connectionTypeDetail_ = value;
        }

        private void clearConnectionTypeDetail() {
            this.connectionTypeDetail_ = getDefaultInstance().getConnectionTypeDetail();
        }

        private void setConnectionTypeDetailBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.connectionTypeDetail_ = value.toStringUtf8();
        }

        public String getPlacementReferenceId() {
            return this.placementReferenceId_;
        }

        public ByteString getPlacementReferenceIdBytes() {
            return ByteString.copyFromUtf8(this.placementReferenceId_);
        }

        private void setPlacementReferenceId(String value) {
            Class<?> valueClass = value.getClass();
            this.placementReferenceId_ = value;
        }

        private void clearPlacementReferenceId() {
            this.placementReferenceId_ = getDefaultInstance().getPlacementReferenceId();
        }

        private void setPlacementReferenceIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.placementReferenceId_ = value.toStringUtf8();
        }

        public String getCreativeId() {
            return this.creativeId_;
        }

        public ByteString getCreativeIdBytes() {
            return ByteString.copyFromUtf8(this.creativeId_);
        }

        private void setCreativeId(String value) {
            Class<?> valueClass = value.getClass();
            this.creativeId_ = value;
        }

        private void clearCreativeId() {
            this.creativeId_ = getDefaultInstance().getCreativeId();
        }

        private void setCreativeIdBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.creativeId_ = value.toStringUtf8();
        }

        public String getConnectionTypeDetailAndroid() {
            return this.connectionTypeDetailAndroid_;
        }

        public ByteString getConnectionTypeDetailAndroidBytes() {
            return ByteString.copyFromUtf8(this.connectionTypeDetailAndroid_);
        }

        private void setConnectionTypeDetailAndroid(String value) {
            Class<?> valueClass = value.getClass();
            this.connectionTypeDetailAndroid_ = value;
        }

        private void clearConnectionTypeDetailAndroid() {
            this.connectionTypeDetailAndroid_ = getDefaultInstance().getConnectionTypeDetailAndroid();
        }

        private void setConnectionTypeDetailAndroidBytes(ByteString value) {
            checkByteStringIsUtf8(value);
            this.connectionTypeDetailAndroid_ = value.toStringUtf8();
        }

        public static SDKError parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKError parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKError parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKError parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKError parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKError parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKError parseFrom(InputStream input) throws IOException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKError parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKError parseDelimitedFrom(InputStream input) throws IOException {
            return (SDKError)parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKError parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKError)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKError parseFrom(CodedInputStream input) throws IOException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKError parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKError)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(SDKError prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE:
                    return new SDKError();
                case NEW_BUILDER:
                    return new Builder();
                case BUILD_MESSAGE_INFO:
                    Object[] objects = new Object[]{"at_", "reason_", "message_", "eventId_", "make_", "model_", "os_", "osVersion_", "connectionType_", "connectionTypeDetail_", "placementReferenceId_", "creativeId_", "connectionTypeDetailAndroid_"};
                    String info = "\u0000\r\u0000\u0000\u0001e\r\u0000\u0000\u0000\u0001\u0002\u0002\f\u0003Ȉ\u0004Ȉ\u0005Ȉ\u0006Ȉ\u0007Ȉ\bȈ\tȈ\nȈ\u000bȈ\fȈeȈ";
                    return newMessageInfo(DEFAULT_INSTANCE, info, objects);
                case GET_DEFAULT_INSTANCE:
                    return DEFAULT_INSTANCE;
                case GET_PARSER:
                    Parser<SDKError> parser = PARSER;
                    if (parser == null) {
                        Class var5 = SDKError.class;
                        synchronized(SDKError.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = (Parser)parser;
                            }
                        }
                    }

                    return parser;
                case GET_MEMOIZED_IS_INITIALIZED:
                    return 1;
                case SET_MEMOIZED_IS_INITIALIZED:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static SDKError getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SDKError> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            SDKError defaultInstance = new SDKError();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(SDKError.class, defaultInstance);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SDKError, Builder> implements SDKErrorOrBuilder {
            private Builder() {
                super(Sdk.SDKError.DEFAULT_INSTANCE);
            }

            public long getAt() {
                return ((SDKError)this.instance).getAt();
            }

            public Builder setAt(long value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setAt(value);
                return this;
            }

            public Builder clearAt() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearAt();
                return this;
            }

            public int getReasonValue() {
                return ((SDKError)this.instance).getReasonValue();
            }

            public Builder setReasonValue(int value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setReasonValue(value);
                return this;
            }

            public Reason getReason() {
                return ((SDKError)this.instance).getReason();
            }

            public Builder setReason(Reason value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setReason(value);
                return this;
            }

            public Builder clearReason() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearReason();
                return this;
            }

            public String getMessage() {
                return ((SDKError)this.instance).getMessage();
            }

            public ByteString getMessageBytes() {
                return ((SDKError)this.instance).getMessageBytes();
            }

            public Builder setMessage(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setMessage(value);
                return this;
            }

            public Builder clearMessage() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearMessage();
                return this;
            }

            public Builder setMessageBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setMessageBytes(value);
                return this;
            }

            public String getEventId() {
                return ((SDKError)this.instance).getEventId();
            }

            public ByteString getEventIdBytes() {
                return ((SDKError)this.instance).getEventIdBytes();
            }

            public Builder setEventId(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setEventId(value);
                return this;
            }

            public Builder clearEventId() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearEventId();
                return this;
            }

            public Builder setEventIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setEventIdBytes(value);
                return this;
            }

            public String getMake() {
                return ((SDKError)this.instance).getMake();
            }

            public ByteString getMakeBytes() {
                return ((SDKError)this.instance).getMakeBytes();
            }

            public Builder setMake(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setMake(value);
                return this;
            }

            public Builder clearMake() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearMake();
                return this;
            }

            public Builder setMakeBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setMakeBytes(value);
                return this;
            }

            public String getModel() {
                return ((SDKError)this.instance).getModel();
            }

            public ByteString getModelBytes() {
                return ((SDKError)this.instance).getModelBytes();
            }

            public Builder setModel(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setModel(value);
                return this;
            }

            public Builder clearModel() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearModel();
                return this;
            }

            public Builder setModelBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setModelBytes(value);
                return this;
            }

            public String getOs() {
                return ((SDKError)this.instance).getOs();
            }

            public ByteString getOsBytes() {
                return ((SDKError)this.instance).getOsBytes();
            }

            public Builder setOs(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setOs(value);
                return this;
            }

            public Builder clearOs() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearOs();
                return this;
            }

            public Builder setOsBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setOsBytes(value);
                return this;
            }

            public String getOsVersion() {
                return ((SDKError)this.instance).getOsVersion();
            }

            public ByteString getOsVersionBytes() {
                return ((SDKError)this.instance).getOsVersionBytes();
            }

            public Builder setOsVersion(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setOsVersion(value);
                return this;
            }

            public Builder clearOsVersion() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearOsVersion();
                return this;
            }

            public Builder setOsVersionBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setOsVersionBytes(value);
                return this;
            }

            public String getConnectionType() {
                return ((SDKError)this.instance).getConnectionType();
            }

            public ByteString getConnectionTypeBytes() {
                return ((SDKError)this.instance).getConnectionTypeBytes();
            }

            public Builder setConnectionType(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionType(value);
                return this;
            }

            public Builder clearConnectionType() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearConnectionType();
                return this;
            }

            public Builder setConnectionTypeBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionTypeBytes(value);
                return this;
            }

            public String getConnectionTypeDetail() {
                return ((SDKError)this.instance).getConnectionTypeDetail();
            }

            public ByteString getConnectionTypeDetailBytes() {
                return ((SDKError)this.instance).getConnectionTypeDetailBytes();
            }

            public Builder setConnectionTypeDetail(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionTypeDetail(value);
                return this;
            }

            public Builder clearConnectionTypeDetail() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearConnectionTypeDetail();
                return this;
            }

            public Builder setConnectionTypeDetailBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionTypeDetailBytes(value);
                return this;
            }

            public String getPlacementReferenceId() {
                return ((SDKError)this.instance).getPlacementReferenceId();
            }

            public ByteString getPlacementReferenceIdBytes() {
                return ((SDKError)this.instance).getPlacementReferenceIdBytes();
            }

            public Builder setPlacementReferenceId(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setPlacementReferenceId(value);
                return this;
            }

            public Builder clearPlacementReferenceId() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearPlacementReferenceId();
                return this;
            }

            public Builder setPlacementReferenceIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setPlacementReferenceIdBytes(value);
                return this;
            }

            public String getCreativeId() {
                return ((SDKError)this.instance).getCreativeId();
            }

            public ByteString getCreativeIdBytes() {
                return ((SDKError)this.instance).getCreativeIdBytes();
            }

            public Builder setCreativeId(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setCreativeId(value);
                return this;
            }

            public Builder clearCreativeId() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearCreativeId();
                return this;
            }

            public Builder setCreativeIdBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setCreativeIdBytes(value);
                return this;
            }

            public String getConnectionTypeDetailAndroid() {
                return ((SDKError)this.instance).getConnectionTypeDetailAndroid();
            }

            public ByteString getConnectionTypeDetailAndroidBytes() {
                return ((SDKError)this.instance).getConnectionTypeDetailAndroidBytes();
            }

            public Builder setConnectionTypeDetailAndroid(String value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionTypeDetailAndroid(value);
                return this;
            }

            public Builder clearConnectionTypeDetailAndroid() {
                this.copyOnWrite();
                ((SDKError)this.instance).clearConnectionTypeDetailAndroid();
                return this;
            }

            public Builder setConnectionTypeDetailAndroidBytes(ByteString value) {
                this.copyOnWrite();
                ((SDKError)this.instance).setConnectionTypeDetailAndroidBytes(value);
                return this;
            }
        }

        public static enum Reason implements Internal.EnumLite {
            UNKNOWN_ERROR(0),
            INVALID_APP_ID(2),
            CURRENTLY_INITIALIZING(3),
            ALREADY_INITIALIZED(4),
            SDK_NOT_INITIALIZED(6),
            USER_AGENT_ERROR(7),
            API_REQUEST_ERROR(101),
            API_RESPONSE_DATA_ERROR(102),
            API_RESPONSE_DECODE_ERROR(103),
            API_FAILED_STATUS_CODE(104),
            INVALID_TEMPLATE_URL(105),
            INVALID_REQUEST_BUILDER_ERROR(106),
            TEMPLATE_UNZIP_ERROR(109),
            INVALID_CTA_URL(110),
            INVALID_ASSET_URL(111),
            ASSET_REQUEST_ERROR(112),
            ASSET_RESPONSE_DATA_ERROR(113),
            ASSET_WRITE_ERROR(114),
            INVALID_INDEX_URL(115),
            GZIP_ENCODE_ERROR(116),
            ASSET_FAILED_STATUS_CODE(117),
            PROTOBUF_SERIALIZATION_ERROR(118),
            JSON_ENCODE_ERROR(119),
            TPAT_ERROR(121),
            INVALID_ADS_ENDPOINT(122),
            INVALID_RI_ENDPOINT(123),
            INVALID_LOG_ERROR_ENDPOINT(124),
            INVALID_METRICS_ENDPOINT(125),
            ASSET_FAILED_INSUFFICIENT_SPACE(126),
            ASSET_FAILED_MAX_SPACE_EXCEEDED(127),
            INVALID_TPAT_KEY(128),
            EMPTY_TPAT_ERROR(129),
            MRAID_DOWNLOAD_JS_ERROR(130),
            MRAID_JS_WRITE_FAILED(131),
            OMSDK_DOWNLOAD_JS_ERROR(132),
            OMSDK_JS_WRITE_FAILED(133),
            STORE_REGION_CODE_ERROR(134),
            INVALID_CONFIG_RESPONSE(135),
            PRIVACY_URL_ERROR(136),
            TPAT_RETRY_FAILED(137),
            CONFIG_REFRESH_FAILED(138),
            INVALID_EVENT_ID_ERROR(200),
            INVALID_PLACEMENT_ID(201),
            AD_CONSUMED(202),
            AD_IS_LOADING(203),
            AD_ALREADY_LOADED(204),
            AD_IS_PLAYING(205),
            AD_ALREADY_FAILED(206),
            PLACEMENT_AD_TYPE_MISMATCH(207),
            INVALID_BID_PAYLOAD(208),
            INVALID_JSON_BID_PAYLOAD(209),
            AD_NOT_LOADED(210),
            PLACEMENT_SLEEP(212),
            INVALID_ADUNIT_BID_PAYLOAD(213),
            INVALID_GZIP_BID_PAYLOAD(214),
            AD_RESPONSE_EMPTY(215),
            AD_RESPONSE_INVALID_TEMPLATE_TYPE(216),
            AD_RESPONSE_TIMED_OUT(217),
            MRAID_JS_DOES_NOT_EXIST(218),
            MRAID_JS_COPY_FAILED(219),
            AD_RESPONSE_RETRY_AFTER(220),
            AD_LOAD_FAIL_RETRY_AFTER(221),
            INVALID_WATERFALL_PLACEMENT_ID(222),
            AD_NO_FILL(10001),
            AD_LOAD_TOO_FREQUENTLY(10002),
            AD_SERVER_ERROR(20001),
            AD_PUBLISHER_MISMATCH(30001),
            AD_INTERNAL_INTEGRATION_ERROR(30002),
            MRAID_ERROR(301),
            INVALID_IFA_STATUS(302),
            AD_EXPIRED(304),
            MRAID_BRIDGE_ERROR(305),
            AD_EXPIRED_ON_PLAY(307),
            AD_WIN_NOTIFICATION_ERROR(308),
            ASSET_FAILED_TO_DELETE(309),
            AD_HTML_FAILED_TO_LOAD(310),
            MRAID_JS_CALL_EMPTY(311),
            DEEPLINK_OPEN_FAILED(312),
            EVALUATE_JAVASCRIPT_FAILED(313),
            LINK_COMMAND_OPEN_FAILED(314),
            JSON_PARAMS_ENCODE_ERROR(315),
            GENERATE_JSON_DATA_ERROR(316),
            AD_CLOSED_TEMPLATE_ERROR(317),
            AD_CLOSED_MISSING_HEARTBEAT(318),
            CONCURRENT_PLAYBACK_UNSUPPORTED(400),
            BANNER_VIEW_INVALID_SIZE(500),
            NATIVE_ASSET_ERROR(600),
            WEB_VIEW_WEB_CONTENT_PROCESS_DID_TERMINATE(2000),
            WEB_VIEW_FAILED_NAVIGATION(2001),
            STORE_KIT_LOAD_ERROR(2002),
            OMSDK_COPY_ERROR(2003),
            STORE_OVERLAY_LOAD_ERROR(2004),
            REACHABILITY_INITIALIZATION_FAILED(2005),
            UNKNOWN_RADIO_ACCESS_TECHNOLOGY(2006),
            STORE_KIT_PRESENTATION_ERROR(2007),
            OUT_OF_MEMORY(3001),
            UNRECOGNIZED(-1);

            public static final int UNKNOWN_ERROR_VALUE = 0;
            public static final int INVALID_APP_ID_VALUE = 2;
            public static final int CURRENTLY_INITIALIZING_VALUE = 3;
            public static final int ALREADY_INITIALIZED_VALUE = 4;
            public static final int SDK_NOT_INITIALIZED_VALUE = 6;
            public static final int USER_AGENT_ERROR_VALUE = 7;
            public static final int API_REQUEST_ERROR_VALUE = 101;
            public static final int API_RESPONSE_DATA_ERROR_VALUE = 102;
            public static final int API_RESPONSE_DECODE_ERROR_VALUE = 103;
            public static final int API_FAILED_STATUS_CODE_VALUE = 104;
            public static final int INVALID_TEMPLATE_URL_VALUE = 105;
            public static final int INVALID_REQUEST_BUILDER_ERROR_VALUE = 106;
            public static final int TEMPLATE_UNZIP_ERROR_VALUE = 109;
            public static final int INVALID_CTA_URL_VALUE = 110;
            public static final int INVALID_ASSET_URL_VALUE = 111;
            public static final int ASSET_REQUEST_ERROR_VALUE = 112;
            public static final int ASSET_RESPONSE_DATA_ERROR_VALUE = 113;
            public static final int ASSET_WRITE_ERROR_VALUE = 114;
            public static final int INVALID_INDEX_URL_VALUE = 115;
            public static final int GZIP_ENCODE_ERROR_VALUE = 116;
            public static final int ASSET_FAILED_STATUS_CODE_VALUE = 117;
            public static final int PROTOBUF_SERIALIZATION_ERROR_VALUE = 118;
            public static final int JSON_ENCODE_ERROR_VALUE = 119;
            public static final int TPAT_ERROR_VALUE = 121;
            public static final int INVALID_ADS_ENDPOINT_VALUE = 122;
            public static final int INVALID_RI_ENDPOINT_VALUE = 123;
            public static final int INVALID_LOG_ERROR_ENDPOINT_VALUE = 124;
            public static final int INVALID_METRICS_ENDPOINT_VALUE = 125;
            public static final int ASSET_FAILED_INSUFFICIENT_SPACE_VALUE = 126;
            public static final int ASSET_FAILED_MAX_SPACE_EXCEEDED_VALUE = 127;
            public static final int INVALID_TPAT_KEY_VALUE = 128;
            public static final int EMPTY_TPAT_ERROR_VALUE = 129;
            public static final int MRAID_DOWNLOAD_JS_ERROR_VALUE = 130;
            public static final int MRAID_JS_WRITE_FAILED_VALUE = 131;
            public static final int OMSDK_DOWNLOAD_JS_ERROR_VALUE = 132;
            public static final int OMSDK_JS_WRITE_FAILED_VALUE = 133;
            public static final int STORE_REGION_CODE_ERROR_VALUE = 134;
            public static final int INVALID_CONFIG_RESPONSE_VALUE = 135;
            public static final int PRIVACY_URL_ERROR_VALUE = 136;
            public static final int TPAT_RETRY_FAILED_VALUE = 137;
            public static final int CONFIG_REFRESH_FAILED_VALUE = 138;
            public static final int INVALID_EVENT_ID_ERROR_VALUE = 200;
            public static final int INVALID_PLACEMENT_ID_VALUE = 201;
            public static final int AD_CONSUMED_VALUE = 202;
            public static final int AD_IS_LOADING_VALUE = 203;
            public static final int AD_ALREADY_LOADED_VALUE = 204;
            public static final int AD_IS_PLAYING_VALUE = 205;
            public static final int AD_ALREADY_FAILED_VALUE = 206;
            public static final int PLACEMENT_AD_TYPE_MISMATCH_VALUE = 207;
            public static final int INVALID_BID_PAYLOAD_VALUE = 208;
            public static final int INVALID_JSON_BID_PAYLOAD_VALUE = 209;
            public static final int AD_NOT_LOADED_VALUE = 210;
            public static final int PLACEMENT_SLEEP_VALUE = 212;
            public static final int INVALID_ADUNIT_BID_PAYLOAD_VALUE = 213;
            public static final int INVALID_GZIP_BID_PAYLOAD_VALUE = 214;
            public static final int AD_RESPONSE_EMPTY_VALUE = 215;
            public static final int AD_RESPONSE_INVALID_TEMPLATE_TYPE_VALUE = 216;
            public static final int AD_RESPONSE_TIMED_OUT_VALUE = 217;
            public static final int MRAID_JS_DOES_NOT_EXIST_VALUE = 218;
            public static final int MRAID_JS_COPY_FAILED_VALUE = 219;
            public static final int AD_RESPONSE_RETRY_AFTER_VALUE = 220;
            public static final int AD_LOAD_FAIL_RETRY_AFTER_VALUE = 221;
            public static final int INVALID_WATERFALL_PLACEMENT_ID_VALUE = 222;
            public static final int AD_NO_FILL_VALUE = 10001;
            public static final int AD_LOAD_TOO_FREQUENTLY_VALUE = 10002;
            public static final int AD_SERVER_ERROR_VALUE = 20001;
            public static final int AD_PUBLISHER_MISMATCH_VALUE = 30001;
            public static final int AD_INTERNAL_INTEGRATION_ERROR_VALUE = 30002;
            public static final int MRAID_ERROR_VALUE = 301;
            public static final int INVALID_IFA_STATUS_VALUE = 302;
            public static final int AD_EXPIRED_VALUE = 304;
            public static final int MRAID_BRIDGE_ERROR_VALUE = 305;
            public static final int AD_EXPIRED_ON_PLAY_VALUE = 307;
            public static final int AD_WIN_NOTIFICATION_ERROR_VALUE = 308;
            public static final int ASSET_FAILED_TO_DELETE_VALUE = 309;
            public static final int AD_HTML_FAILED_TO_LOAD_VALUE = 310;
            public static final int MRAID_JS_CALL_EMPTY_VALUE = 311;
            public static final int DEEPLINK_OPEN_FAILED_VALUE = 312;
            public static final int EVALUATE_JAVASCRIPT_FAILED_VALUE = 313;
            public static final int LINK_COMMAND_OPEN_FAILED_VALUE = 314;
            public static final int JSON_PARAMS_ENCODE_ERROR_VALUE = 315;
            public static final int GENERATE_JSON_DATA_ERROR_VALUE = 316;
            public static final int AD_CLOSED_TEMPLATE_ERROR_VALUE = 317;
            public static final int AD_CLOSED_MISSING_HEARTBEAT_VALUE = 318;
            public static final int CONCURRENT_PLAYBACK_UNSUPPORTED_VALUE = 400;
            public static final int BANNER_VIEW_INVALID_SIZE_VALUE = 500;
            public static final int NATIVE_ASSET_ERROR_VALUE = 600;
            public static final int WEB_VIEW_WEB_CONTENT_PROCESS_DID_TERMINATE_VALUE = 2000;
            public static final int WEB_VIEW_FAILED_NAVIGATION_VALUE = 2001;
            public static final int STORE_KIT_LOAD_ERROR_VALUE = 2002;
            public static final int OMSDK_COPY_ERROR_VALUE = 2003;
            public static final int STORE_OVERLAY_LOAD_ERROR_VALUE = 2004;
            public static final int REACHABILITY_INITIALIZATION_FAILED_VALUE = 2005;
            public static final int UNKNOWN_RADIO_ACCESS_TECHNOLOGY_VALUE = 2006;
            public static final int STORE_KIT_PRESENTATION_ERROR_VALUE = 2007;
            public static final int OUT_OF_MEMORY_VALUE = 3001;
            private static final Internal.EnumLiteMap<Reason> internalValueMap = new Internal.EnumLiteMap<Reason>() {
                public Reason findValueByNumber(int number) {
                    return Sdk.SDKError.Reason.forNumber(number);
                }
            };
            private final int value;

            public final int getNumber() {
                if (this == UNRECOGNIZED) {
                    throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
                } else {
                    return this.value;
                }
            }

            /** @deprecated */
            @Deprecated
            public static Reason valueOf(int value) {
                return forNumber(value);
            }

            public static Reason forNumber(int value) {
                switch (value) {
                    case 0:
                        return UNKNOWN_ERROR;
                    case 2:
                        return INVALID_APP_ID;
                    case 3:
                        return CURRENTLY_INITIALIZING;
                    case 4:
                        return ALREADY_INITIALIZED;
                    case 6:
                        return SDK_NOT_INITIALIZED;
                    case 7:
                        return USER_AGENT_ERROR;
                    case 101:
                        return API_REQUEST_ERROR;
                    case 102:
                        return API_RESPONSE_DATA_ERROR;
                    case 103:
                        return API_RESPONSE_DECODE_ERROR;
                    case 104:
                        return API_FAILED_STATUS_CODE;
                    case 105:
                        return INVALID_TEMPLATE_URL;
                    case 106:
                        return INVALID_REQUEST_BUILDER_ERROR;
                    case 109:
                        return TEMPLATE_UNZIP_ERROR;
                    case 110:
                        return INVALID_CTA_URL;
                    case 111:
                        return INVALID_ASSET_URL;
                    case 112:
                        return ASSET_REQUEST_ERROR;
                    case 113:
                        return ASSET_RESPONSE_DATA_ERROR;
                    case 114:
                        return ASSET_WRITE_ERROR;
                    case 115:
                        return INVALID_INDEX_URL;
                    case 116:
                        return GZIP_ENCODE_ERROR;
                    case 117:
                        return ASSET_FAILED_STATUS_CODE;
                    case 118:
                        return PROTOBUF_SERIALIZATION_ERROR;
                    case 119:
                        return JSON_ENCODE_ERROR;
                    case 121:
                        return TPAT_ERROR;
                    case 122:
                        return INVALID_ADS_ENDPOINT;
                    case 123:
                        return INVALID_RI_ENDPOINT;
                    case 124:
                        return INVALID_LOG_ERROR_ENDPOINT;
                    case 125:
                        return INVALID_METRICS_ENDPOINT;
                    case 126:
                        return ASSET_FAILED_INSUFFICIENT_SPACE;
                    case 127:
                        return ASSET_FAILED_MAX_SPACE_EXCEEDED;
                    case 128:
                        return INVALID_TPAT_KEY;
                    case 129:
                        return EMPTY_TPAT_ERROR;
                    case 130:
                        return MRAID_DOWNLOAD_JS_ERROR;
                    case 131:
                        return MRAID_JS_WRITE_FAILED;
                    case 132:
                        return OMSDK_DOWNLOAD_JS_ERROR;
                    case 133:
                        return OMSDK_JS_WRITE_FAILED;
                    case 134:
                        return STORE_REGION_CODE_ERROR;
                    case 135:
                        return INVALID_CONFIG_RESPONSE;
                    case 136:
                        return PRIVACY_URL_ERROR;
                    case 137:
                        return TPAT_RETRY_FAILED;
                    case 138:
                        return CONFIG_REFRESH_FAILED;
                    case 200:
                        return INVALID_EVENT_ID_ERROR;
                    case 201:
                        return INVALID_PLACEMENT_ID;
                    case 202:
                        return AD_CONSUMED;
                    case 203:
                        return AD_IS_LOADING;
                    case 204:
                        return AD_ALREADY_LOADED;
                    case 205:
                        return AD_IS_PLAYING;
                    case 206:
                        return AD_ALREADY_FAILED;
                    case 207:
                        return PLACEMENT_AD_TYPE_MISMATCH;
                    case 208:
                        return INVALID_BID_PAYLOAD;
                    case 209:
                        return INVALID_JSON_BID_PAYLOAD;
                    case 210:
                        return AD_NOT_LOADED;
                    case 212:
                        return PLACEMENT_SLEEP;
                    case 213:
                        return INVALID_ADUNIT_BID_PAYLOAD;
                    case 214:
                        return INVALID_GZIP_BID_PAYLOAD;
                    case 215:
                        return AD_RESPONSE_EMPTY;
                    case 216:
                        return AD_RESPONSE_INVALID_TEMPLATE_TYPE;
                    case 217:
                        return AD_RESPONSE_TIMED_OUT;
                    case 218:
                        return MRAID_JS_DOES_NOT_EXIST;
                    case 219:
                        return MRAID_JS_COPY_FAILED;
                    case 220:
                        return AD_RESPONSE_RETRY_AFTER;
                    case 221:
                        return AD_LOAD_FAIL_RETRY_AFTER;
                    case 222:
                        return INVALID_WATERFALL_PLACEMENT_ID;
                    case 301:
                        return MRAID_ERROR;
                    case 302:
                        return INVALID_IFA_STATUS;
                    case 304:
                        return AD_EXPIRED;
                    case 305:
                        return MRAID_BRIDGE_ERROR;
                    case 307:
                        return AD_EXPIRED_ON_PLAY;
                    case 308:
                        return AD_WIN_NOTIFICATION_ERROR;
                    case 309:
                        return ASSET_FAILED_TO_DELETE;
                    case 310:
                        return AD_HTML_FAILED_TO_LOAD;
                    case 311:
                        return MRAID_JS_CALL_EMPTY;
                    case 312:
                        return DEEPLINK_OPEN_FAILED;
                    case 313:
                        return EVALUATE_JAVASCRIPT_FAILED;
                    case 314:
                        return LINK_COMMAND_OPEN_FAILED;
                    case 315:
                        return JSON_PARAMS_ENCODE_ERROR;
                    case 316:
                        return GENERATE_JSON_DATA_ERROR;
                    case 317:
                        return AD_CLOSED_TEMPLATE_ERROR;
                    case 318:
                        return AD_CLOSED_MISSING_HEARTBEAT;
                    case 400:
                        return CONCURRENT_PLAYBACK_UNSUPPORTED;
                    case 500:
                        return BANNER_VIEW_INVALID_SIZE;
                    case 600:
                        return NATIVE_ASSET_ERROR;
                    case 2000:
                        return WEB_VIEW_WEB_CONTENT_PROCESS_DID_TERMINATE;
                    case 2001:
                        return WEB_VIEW_FAILED_NAVIGATION;
                    case 2002:
                        return STORE_KIT_LOAD_ERROR;
                    case 2003:
                        return OMSDK_COPY_ERROR;
                    case 2004:
                        return STORE_OVERLAY_LOAD_ERROR;
                    case 2005:
                        return REACHABILITY_INITIALIZATION_FAILED;
                    case 2006:
                        return UNKNOWN_RADIO_ACCESS_TECHNOLOGY;
                    case 2007:
                        return STORE_KIT_PRESENTATION_ERROR;
                    case 3001:
                        return OUT_OF_MEMORY;
                    case 10001:
                        return AD_NO_FILL;
                    case 10002:
                        return AD_LOAD_TOO_FREQUENTLY;
                    case 20001:
                        return AD_SERVER_ERROR;
                    case 30001:
                        return AD_PUBLISHER_MISMATCH;
                    case 30002:
                        return AD_INTERNAL_INTEGRATION_ERROR;
                    default:
                        return null;
                }
            }

            public static Internal.EnumLiteMap<Reason> internalGetValueMap() {
                return internalValueMap;
            }

            public static Internal.EnumVerifier internalGetVerifier() {
                return Sdk.SDKError.Reason.ReasonVerifier.INSTANCE;
            }

            private Reason(int value) {
                this.value = value;
            }

            private static final class ReasonVerifier implements Internal.EnumVerifier {
                static final Internal.EnumVerifier INSTANCE = new ReasonVerifier();

                private ReasonVerifier() {
                }

                public boolean isInRange(int number) {
                    return Sdk.SDKError.Reason.forNumber(number) != null;
                }
            }
        }
    }

    public interface SDKErrorOrBuilder extends MessageLiteOrBuilder {
        long getAt();

        int getReasonValue();

        SDKError.Reason getReason();

        String getMessage();

        ByteString getMessageBytes();

        String getEventId();

        ByteString getEventIdBytes();

        String getMake();

        ByteString getMakeBytes();

        String getModel();

        ByteString getModelBytes();

        String getOs();

        ByteString getOsBytes();

        String getOsVersion();

        ByteString getOsVersionBytes();

        String getConnectionType();

        ByteString getConnectionTypeBytes();

        String getConnectionTypeDetail();

        ByteString getConnectionTypeDetailBytes();

        String getPlacementReferenceId();

        ByteString getPlacementReferenceIdBytes();

        String getCreativeId();

        ByteString getCreativeIdBytes();

        String getConnectionTypeDetailAndroid();

        ByteString getConnectionTypeDetailAndroidBytes();
    }

    public static final class SDKErrorBatch extends GeneratedMessageLite<SDKErrorBatch, SDKErrorBatch.Builder> implements SDKErrorBatchOrBuilder {
        public static final int ERRORS_FIELD_NUMBER = 1;
        private Internal.ProtobufList<SDKError> errors_ = emptyProtobufList();
        private static final SDKErrorBatch DEFAULT_INSTANCE;
        private static volatile Parser<SDKErrorBatch> PARSER;

        private SDKErrorBatch() {
        }

        public List<SDKError> getErrorsList() {
            return this.errors_;
        }

        public List<? extends SDKErrorOrBuilder> getErrorsOrBuilderList() {
            return this.errors_;
        }

        public int getErrorsCount() {
            return this.errors_.size();
        }

        public SDKError getErrors(int index) {
            return (SDKError)this.errors_.get(index);
        }

        public SDKErrorOrBuilder getErrorsOrBuilder(int index) {
            return (SDKErrorOrBuilder)this.errors_.get(index);
        }

        private void ensureErrorsIsMutable() {
            Internal.ProtobufList<SDKError> tmp = this.errors_;
            if (!tmp.isModifiable()) {
                this.errors_ = GeneratedMessageLite.mutableCopy(tmp);
            }

        }

        private void setErrors(int index, SDKError value) {
            value.getClass();
            this.ensureErrorsIsMutable();
            this.errors_.set(index, value);
        }

        private void addErrors(SDKError value) {
            value.getClass();
            this.ensureErrorsIsMutable();
            this.errors_.add(value);
        }

        private void addErrors(int index, SDKError value) {
            value.getClass();
            this.ensureErrorsIsMutable();
            this.errors_.add(index, value);
        }

        private void addAllErrors(Iterable<? extends SDKError> values) {
            this.ensureErrorsIsMutable();
            AbstractMessageLite.addAll(values, this.errors_);
        }

        private void clearErrors() {
            this.errors_ = emptyProtobufList();
        }

        private void removeErrors(int index) {
            this.ensureErrorsIsMutable();
            this.errors_.remove(index);
        }

        public static SDKErrorBatch parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKErrorBatch parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKErrorBatch parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKErrorBatch parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKErrorBatch parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
        }

        public static SDKErrorBatch parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
        }

        public static SDKErrorBatch parseFrom(InputStream input) throws IOException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKErrorBatch parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKErrorBatch parseDelimitedFrom(InputStream input) throws IOException {
            return (SDKErrorBatch)parseDelimitedFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKErrorBatch parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKErrorBatch)parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static SDKErrorBatch parseFrom(CodedInputStream input) throws IOException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
        }

        public static SDKErrorBatch parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return (SDKErrorBatch)GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return (Builder)DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(SDKErrorBatch prototype) {
            return (Builder)DEFAULT_INSTANCE.createBuilder(prototype);
        }

        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke method, Object arg0, Object arg1) {
            switch (method) {
                case NEW_MUTABLE_INSTANCE:
                    return new SDKErrorBatch();
                case NEW_BUILDER:
                    return new Builder();
                case BUILD_MESSAGE_INFO:
                    Object[] objects = new Object[]{"errors_", SDKError.class};
                    String info = "\u0000\u0001\u0000\u0000\u0001\u0001\u0001\u0000\u0001\u0000\u0001\u001b";
                    return newMessageInfo(DEFAULT_INSTANCE, info, objects);
                case GET_DEFAULT_INSTANCE:
                    return DEFAULT_INSTANCE;
                case GET_PARSER:
                    Parser<SDKErrorBatch> parser = PARSER;
                    if (parser == null) {
                        Class var5 = SDKErrorBatch.class;
                        synchronized(SDKErrorBatch.class) {
                            parser = PARSER;
                            if (parser == null) {
                                parser = new GeneratedMessageLite.DefaultInstanceBasedParser(DEFAULT_INSTANCE);
                                PARSER = (Parser)parser;
                            }
                        }
                    }

                    return parser;
                case GET_MEMOIZED_IS_INITIALIZED:
                    return 1;
                case SET_MEMOIZED_IS_INITIALIZED:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static SDKErrorBatch getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<SDKErrorBatch> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }

        static {
            SDKErrorBatch defaultInstance = new SDKErrorBatch();
            DEFAULT_INSTANCE = defaultInstance;
            GeneratedMessageLite.registerDefaultInstance(SDKErrorBatch.class, defaultInstance);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<SDKErrorBatch, Builder> implements SDKErrorBatchOrBuilder {
            private Builder() {
                super(Sdk.SDKErrorBatch.DEFAULT_INSTANCE);
            }

            public List<SDKError> getErrorsList() {
                return Collections.unmodifiableList(((SDKErrorBatch)this.instance).getErrorsList());
            }

            public int getErrorsCount() {
                return ((SDKErrorBatch)this.instance).getErrorsCount();
            }

            public SDKError getErrors(int index) {
                return ((SDKErrorBatch)this.instance).getErrors(index);
            }

            public Builder setErrors(int index, SDKError value) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).setErrors(index, value);
                return this;
            }

            public Builder setErrors(int index, SDKError.Builder builderForValue) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).setErrors(index, (SDKError)builderForValue.build());
                return this;
            }

            public Builder addErrors(SDKError value) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).addErrors(value);
                return this;
            }

            public Builder addErrors(int index, SDKError value) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).addErrors(index, value);
                return this;
            }

            public Builder addErrors(SDKError.Builder builderForValue) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).addErrors((SDKError)builderForValue.build());
                return this;
            }

            public Builder addErrors(int index, SDKError.Builder builderForValue) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).addErrors(index, (SDKError)builderForValue.build());
                return this;
            }

            public Builder addAllErrors(Iterable<? extends SDKError> values) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).addAllErrors(values);
                return this;
            }

            public Builder clearErrors() {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).clearErrors();
                return this;
            }

            public Builder removeErrors(int index) {
                this.copyOnWrite();
                ((SDKErrorBatch)this.instance).removeErrors(index);
                return this;
            }
        }
    }

    public interface SDKErrorBatchOrBuilder extends MessageLiteOrBuilder {
        List<SDKError> getErrorsList();

        SDKError getErrors(int var1);

        int getErrorsCount();
    }
}
