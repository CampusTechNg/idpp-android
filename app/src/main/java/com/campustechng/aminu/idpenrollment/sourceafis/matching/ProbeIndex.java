package com.campustechng.aminu.idpenrollment.sourceafis.matching;

import com.campustechng.aminu.idpenrollment.sourceafis.matching.minutia.EdgeHash;
import com.campustechng.aminu.idpenrollment.sourceafis.matching.minutia.EdgeTable;
import com.campustechng.aminu.idpenrollment.sourceafis.templates.Template;

public final class ProbeIndex
{
    public Template template;
    public EdgeTable edges;
    public EdgeHash edgeHash;
}
