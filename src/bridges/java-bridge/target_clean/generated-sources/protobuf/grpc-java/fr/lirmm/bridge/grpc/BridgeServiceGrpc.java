package fr.lirmm.bridge.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.60.0)",
    comments = "Source: bridge.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BridgeServiceGrpc {

  private BridgeServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "fr.lirmm.bridge.grpc.BridgeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.FunctionRequest,
      fr.lirmm.bridge.grpc.FunctionResponse> getExecuteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Execute",
      requestType = fr.lirmm.bridge.grpc.FunctionRequest.class,
      responseType = fr.lirmm.bridge.grpc.FunctionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.FunctionRequest,
      fr.lirmm.bridge.grpc.FunctionResponse> getExecuteMethod() {
    io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.FunctionRequest, fr.lirmm.bridge.grpc.FunctionResponse> getExecuteMethod;
    if ((getExecuteMethod = BridgeServiceGrpc.getExecuteMethod) == null) {
      synchronized (BridgeServiceGrpc.class) {
        if ((getExecuteMethod = BridgeServiceGrpc.getExecuteMethod) == null) {
          BridgeServiceGrpc.getExecuteMethod = getExecuteMethod =
              io.grpc.MethodDescriptor.<fr.lirmm.bridge.grpc.FunctionRequest, fr.lirmm.bridge.grpc.FunctionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Execute"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fr.lirmm.bridge.grpc.FunctionRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fr.lirmm.bridge.grpc.FunctionResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BridgeServiceMethodDescriptorSupplier("Execute"))
              .build();
        }
      }
    }
    return getExecuteMethod;
  }

  private static volatile io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.Empty,
      fr.lirmm.bridge.grpc.FunctionList> getListFunctionsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListFunctions",
      requestType = fr.lirmm.bridge.grpc.Empty.class,
      responseType = fr.lirmm.bridge.grpc.FunctionList.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.Empty,
      fr.lirmm.bridge.grpc.FunctionList> getListFunctionsMethod() {
    io.grpc.MethodDescriptor<fr.lirmm.bridge.grpc.Empty, fr.lirmm.bridge.grpc.FunctionList> getListFunctionsMethod;
    if ((getListFunctionsMethod = BridgeServiceGrpc.getListFunctionsMethod) == null) {
      synchronized (BridgeServiceGrpc.class) {
        if ((getListFunctionsMethod = BridgeServiceGrpc.getListFunctionsMethod) == null) {
          BridgeServiceGrpc.getListFunctionsMethod = getListFunctionsMethod =
              io.grpc.MethodDescriptor.<fr.lirmm.bridge.grpc.Empty, fr.lirmm.bridge.grpc.FunctionList>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListFunctions"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fr.lirmm.bridge.grpc.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  fr.lirmm.bridge.grpc.FunctionList.getDefaultInstance()))
              .setSchemaDescriptor(new BridgeServiceMethodDescriptorSupplier("ListFunctions"))
              .build();
        }
      }
    }
    return getListFunctionsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BridgeServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BridgeServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BridgeServiceStub>() {
        @java.lang.Override
        public BridgeServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BridgeServiceStub(channel, callOptions);
        }
      };
    return BridgeServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BridgeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BridgeServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BridgeServiceBlockingStub>() {
        @java.lang.Override
        public BridgeServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BridgeServiceBlockingStub(channel, callOptions);
        }
      };
    return BridgeServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BridgeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BridgeServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BridgeServiceFutureStub>() {
        @java.lang.Override
        public BridgeServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BridgeServiceFutureStub(channel, callOptions);
        }
      };
    return BridgeServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Execute a function by name with JSON arguments
     * </pre>
     */
    default void execute(fr.lirmm.bridge.grpc.FunctionRequest request,
        io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteMethod(), responseObserver);
    }

    /**
     * <pre>
     * List available functions (for discovery)
     * </pre>
     */
    default void listFunctions(fr.lirmm.bridge.grpc.Empty request,
        io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionList> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getListFunctionsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service BridgeService.
   */
  public static abstract class BridgeServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return BridgeServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service BridgeService.
   */
  public static final class BridgeServiceStub
      extends io.grpc.stub.AbstractAsyncStub<BridgeServiceStub> {
    private BridgeServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BridgeServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BridgeServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Execute a function by name with JSON arguments
     * </pre>
     */
    public void execute(fr.lirmm.bridge.grpc.FunctionRequest request,
        io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * List available functions (for discovery)
     * </pre>
     */
    public void listFunctions(fr.lirmm.bridge.grpc.Empty request,
        io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionList> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getListFunctionsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service BridgeService.
   */
  public static final class BridgeServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<BridgeServiceBlockingStub> {
    private BridgeServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BridgeServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BridgeServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Execute a function by name with JSON arguments
     * </pre>
     */
    public fr.lirmm.bridge.grpc.FunctionResponse execute(fr.lirmm.bridge.grpc.FunctionRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * List available functions (for discovery)
     * </pre>
     */
    public fr.lirmm.bridge.grpc.FunctionList listFunctions(fr.lirmm.bridge.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getListFunctionsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service BridgeService.
   */
  public static final class BridgeServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<BridgeServiceFutureStub> {
    private BridgeServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BridgeServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BridgeServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Execute a function by name with JSON arguments
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<fr.lirmm.bridge.grpc.FunctionResponse> execute(
        fr.lirmm.bridge.grpc.FunctionRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * List available functions (for discovery)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<fr.lirmm.bridge.grpc.FunctionList> listFunctions(
        fr.lirmm.bridge.grpc.Empty request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getListFunctionsMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE = 0;
  private static final int METHODID_LIST_FUNCTIONS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE:
          serviceImpl.execute((fr.lirmm.bridge.grpc.FunctionRequest) request,
              (io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionResponse>) responseObserver);
          break;
        case METHODID_LIST_FUNCTIONS:
          serviceImpl.listFunctions((fr.lirmm.bridge.grpc.Empty) request,
              (io.grpc.stub.StreamObserver<fr.lirmm.bridge.grpc.FunctionList>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExecuteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              fr.lirmm.bridge.grpc.FunctionRequest,
              fr.lirmm.bridge.grpc.FunctionResponse>(
                service, METHODID_EXECUTE)))
        .addMethod(
          getListFunctionsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              fr.lirmm.bridge.grpc.Empty,
              fr.lirmm.bridge.grpc.FunctionList>(
                service, METHODID_LIST_FUNCTIONS)))
        .build();
  }

  private static abstract class BridgeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BridgeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return fr.lirmm.bridge.grpc.BridgeProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BridgeService");
    }
  }

  private static final class BridgeServiceFileDescriptorSupplier
      extends BridgeServiceBaseDescriptorSupplier {
    BridgeServiceFileDescriptorSupplier() {}
  }

  private static final class BridgeServiceMethodDescriptorSupplier
      extends BridgeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    BridgeServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BridgeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BridgeServiceFileDescriptorSupplier())
              .addMethod(getExecuteMethod())
              .addMethod(getListFunctionsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
