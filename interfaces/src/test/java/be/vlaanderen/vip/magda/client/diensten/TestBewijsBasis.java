package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Beroepskwalificatie;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Bewijs;
import be.vlaanderen.vip.magda.client.domain.geefbewijzen.Deelkwalificatie;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.Month;
import java.time.Year;
import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode
class TestBewijsBasis implements Bewijs.Basis {

    public static final TestBewijsBasis COMPLETE = new TestBewijsBasis(true);
    public static final TestBewijsBasis WITH_NULL_VALUES = new TestBewijsBasis(false);

    private final boolean complete;

    @Nullable
    @Override
    public List<Bewijs.AlternatieveInstantie> alternatieveInstanties() {
        if(!complete) {
            return null;
        }

        return List.of(new Bewijs.AlternatieveInstantie() {

            @Override
            public Bewijs.Naam instantierol() {
                return () -> "alternatieveInstantie 1 instantierol naam";
            }

            @Override
            public Bewijs.Naam instantie() {
                return () -> "alternatieveInstantie 1 instantie naam";
            }
        }, new Bewijs.AlternatieveInstantie() {

            @Override
            public Bewijs.Naam instantierol() {
                return () -> "alternatieveInstantie 2 instantierol naam";
            }

            @Override
            public Bewijs.Naam instantie() {
                return () -> "alternatieveInstantie 2 instantie naam";
            }
        });
    }

    @Nullable
    @Override
    public List<Beroepskwalificatie> beroepskwalificaties() {
        if(!complete) {
            return null;
        }

        return List.of(new Beroepskwalificatie() {

            @Override
            public String naam() {
                return "beroepskwalificatie 1 naam";
            }

            @Override
            public String code() {
                return "beroepskwalificatie 1 code";
            }
        }, new Beroepskwalificatie() {

            @Override
            public String naam() {
                return "beroepskwalificatie 2 naam";
            }

            @Override
            public String code() {
                return "beroepskwalificatie 2 code";
            }
        });
    }

    @Nullable
    @Override
    public List<Deelkwalificatie> deelkwalificaties() {
        if(!complete) {
            return null;
        }

        return List.of(new Deelkwalificatie() {

            @Override
            public String naam() {
                return "deelkwalificatie 1 naam";
            }

            @Override
            public String code() {
                return "deelkwalificatie 1 code";
            }
        }, new Deelkwalificatie() {

            @Override
            public String naam() {
                return "deelkwalificatie 2 naam";
            }

            @Override
            public String code() {
                return "deelkwalificatie 2 code";
            }
        });

    }

    @Override
    public Bewijs.Naam authenticiteit() {
        return () -> "authenticiteit naam";
    }

    @Override
    public Bewijs.Naam bewijsstaat() {
        return () -> "bewijsstaat naam";
    }

    @Override
    public Bewijs.Naam bewijstype() {
        return () -> "bewijstype naam";
    }

    @Nullable
    @Override
    public List<Bewijs.BijkomendeInformatie> bijkomendeInformaties() {
        if(!complete) {
            return null;
        }

        return List.of(new Bewijs.BijkomendeInformatie() {

            @Override
            public String naam() {
                return "bijkomendeInformatie 1 naam";
            }

            @Override
            public String inhoud() {
                return "bijkomendeInformatie 1 inhoud";
            }
        }, new Bewijs.BijkomendeInformatie() {

            @Override
            public String naam() {
                return "bijkomendeInformatie 2 naam";
            }

            @Override
            public String inhoud() {
                return "bijkomendeInformatie 2 inhoud";
            }
        });
    }

    @Override
    public Bewijs.Naam categorie() {
        return () -> "categorie naam";
    }

    @Nullable
    @Override
    public Bewijs.NaamEnOptioneleCode detailOnderwerp() {
        if(!complete) {
            return null;
        }

        return new Bewijs.NaamEnOptioneleCode() {

            @Nullable
            @Override
            public String code() {
                return "detailOnderwerp code";
            }

            @Override
            public String naam() {
                return "detailOnderwerp naam";
            }
        };
    }

    @Override
    public Bewijs.Naam graad() {
        return () -> "graad naam";
    }

    @Override
    public Bewijs.Naam instantie() {
        return () -> "instantie naam";
    }

    @Override
    public Bewijs.Instelling instelling() {
        return new Bewijs.Instelling() {

            @Override
            public String naam() {
                return "instelling naam";
            }

            @Override
            @Nullable
            public String nummer() {
                return complete ? "instelling nummer" : null;
            }
        };
    }

    @Override
    public Bewijs.Code land() {
        return () -> "land code";
    }

    @Override
    public Bewijs.CodeEnOptioneleNaam onderwerp() {
        return new Bewijs.CodeEnOptioneleNaam() {

            @Override
            public String code() {
                return "onderwerp code";
            }

            @Nullable
            @Override
            public String naam() {
                return complete ? "onderwerp naam" : null;
            }
        };
    }

    @Override
    public Bewijs.Naam onderwijsvorm() {
        return () -> "onderwijsvorm naam";
    }

    @Override
    public Bewijs.Naam schooltype() {
        return () -> "schooltype naam";
    }

    @Nullable
    @Override
    public Bewijs.NaamEnOptioneleCode specialisatie() {
        if(!complete) {
            return null;
        }

        return new Bewijs.NaamEnOptioneleCode() {

            @Nullable
            @Override
            public String code() {
                return "specialisatie code";
            }

            @Override
            public String naam() {
                return "specialisatie naam";
            }
        };
    }

    @Nullable
    @Override
    public Bewijs.NaamEnOptioneleCode studierichting() {
        if(!complete) {
            return null;
        }

        return new Bewijs.NaamEnOptioneleCode() {

            @Nullable
            @Override
            public String code() {
                return "studierichting code";
            }

            @Override
            public String naam() {
                return "studierichting naam";
            }
        };
    }

    @Override
    public Bewijs.Code taal() {
        return () -> "taal code";
    }

    @Nullable
    @Override
    public String vervalperiode() {
        return complete ? "vervalperiode" : null;
    }

    @Override
    public String volledigeNaam() {
        return "volledigeNaam";
    }

    @Override
    public Bewijs.Uitreikingsdatum uitreikingsdatum() {
        return new Bewijs.Uitreikingsdatum() {

            @Override
            public Year jaar() {
                return Year.of(2025);
            }

            @Nullable
            @Override
            public Month maand() {
                return complete ? Month.JULY : null;
            }

            @Nullable
            @Override
            public Integer dag() {
                return complete ? 5 : null;
            }
        };
    }

    @Nullable
    @Override
    public Integer urenVolwassenenonderwijs() {
        return complete ? 123 : null;
    }
}
