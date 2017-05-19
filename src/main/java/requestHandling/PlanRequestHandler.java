package requestHandling;

import constants.Params;
import contracts.PlanRequest;
import data.MatsimDataProvider;
import data.MatsimDataReader;

public class PlanRequestHandler extends AbstractPostRequestHandler<PlanRequest>{

    public PlanRequestHandler(MatsimDataProvider data) {
        super(PlanRequest.class, data);
    }

    @Override
    public Answer process(PlanRequest body) {

        Object result = dataProvider.getPlan(body.getTimestep(), body.getIndex());
        return new Answer(Params.STATUS_INTERNAL_SERVER_ERROR, "not yet implemented");
    }
}
